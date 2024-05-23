package pl.put.szlaki.domain


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private data class TrailToParse(
    val id: Long,
    var name: String,
    val waypoints: MutableList<TrailWaypoint>,
    val segments: MutableList<TrailSegmentToParse>
)

private data class TrailSegmentToParse(
    val id: Long,
    var points: MutableList<TrailPoint>
)

//Parsing Date
private fun parseDate(string: String?): LocalDateTime? {
    return try {
        LocalDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
    catch (e: Exception) {
        null;
    }

}

//Get trail from gpx
private fun getTrailFromGPX(string: String): TrailToParse {
    // Regex
    val nameEx = Regex("<name>(?<name>.*?)</name>")
    val waypointEx =
        Regex("<wpt lat=\"(?<latitude>.*?)\" lon=\"(?<longitude>.*?)\">(?<attributes>(.|[\n ])*?)</wpt>")
    val trackPointEx =
        Regex("<trkpt lat=\"(?<latitude>.*?)\" lon=\"(?<longitude>.*?)\">(?<attributes>(.|[\n ])*?)</trkpt>")
    val elevationEx = Regex("<ele>(?<elevation>.*?)</ele>")
    val timeEx = Regex("<time>(?<time>.*?)</time>")
    val trackSegmentEx = Regex("<trkseg>(?<segment>(.|[\n ])*?)</trkseg>")

    val trail = TrailToParse(0, "", mutableListOf(), mutableListOf())

    trail.name = nameEx.find(string)!!.groups["name"]!!.value
    //add points
    fun addPoints() {
        var id: Long = 0;
        for (pt in trackPointEx.findAll(string)) {
            val point = TrailPoint(
                id = id++,
                latitude = pt.groups["latitude"]!!.value.toFloat(),
                longitude = pt.groups["longitude"]!!.value.toFloat(),
                elevation = elevationEx.find(pt.groups["attributes"].toString())!!.groups["elevation"]!!.value.toFloat(),
                time = parseDate(timeEx.find(pt.groups["attributes"].toString())!!.groups["time"]?.value.toString())
                //                                                                      toString()= String?->String
            )
            trail.segments.last().points.add(point)
        }
    }


    var id: Long = 0
    //Waypoints
    for (wpt in waypointEx.findAll(string)) {
        val point = TrailPoint(
            id = id++,
            latitude = wpt.groups["latitude"]!!.value.toFloat(),
            longitude = wpt.groups["longitude"]!!.value.toFloat(),
            elevation = elevationEx.find(wpt.groups["attributes"].toString())!!.groups["elevation"]!!.value.toFloat(),
            time = parseDate(timeEx.find(wpt.groups["attributes"].toString())!!.groups["time"]?.value)
        )
        val waypoint = TrailWaypoint(
            name = nameEx.find(wpt.groups["attributes"].toString())!!.groups["name"]!!.value,
            point
        )
        trail.waypoints.add(waypoint)
    }

    //Segments
    val segments = trackSegmentEx.findAll(string)
    if (segments.toList().isEmpty()) {//If GPX file doesn't have segments
        trail.segments.add(TrailSegmentToParse(0, mutableListOf()))
        addPoints()
    } else {
        id = 0
        for (seg in segments) {
            trail.segments.add(TrailSegmentToParse(id++, mutableListOf()))
            addPoints()
        }
    }

    return trail
}

private fun breakIntoSegments(trail: TrailToParse) {
    if (trail.segments.size == 1 && trail.segments[0].points.size > 50) {
        var id: Long = 1
        val points = trail.segments[0].points.withIndex()
        //                  List              -> Map        -> List inside ->List element
        val pointsList = points.groupBy { it.index / 50 }.map { it.value.map { it.value } }
        trail.segments[0].points = pointsList[0].toMutableList()
        for (i in 1..<pointsList.size) {
            trail.segments.add(TrailSegmentToParse(id++, pointsList[i].toMutableList()))
        }
    }
}

fun parseTrail(string: String): Trail {
    val trail= getTrailFromGPX(string)

    //if there weren't any segments in original file break trail into them
    breakIntoSegments(trail)

    //trail bounds and length
    val bounds=Bounds(-90f,-180f,90f,180f)
    var length=0.0

    //Calculate segment info
    fun calculateSegmentInfo(segment: TrailSegmentToParse): TrailSegment {
        val upAndDown = floatArrayOf(0F, 0F)
        var elevationPrevious = segment.points[0].elevation
        var elevationSum = elevationPrevious
        var path = 0.0
        val earthRadius = 6371.01
        for (i in 1..<segment.points.size) {
            //Distance - globe
            val lat1 = Math.toRadians(segment.points[i].latitude.toDouble())
            val lon1 = Math.toRadians(segment.points[i].longitude.toDouble())
            val lat2 = Math.toRadians(segment.points[i - 1].latitude.toDouble())
            val lon2 = Math.toRadians(segment.points[i - 1].longitude.toDouble())
            val straightPath = 2 * earthRadius * asin(
                sqrt(
                    sin((lat1 - lat2) / 2).pow(2) + cos(lat1) * cos(lat2) * sin((lon1 - lon2) / 2).pow(
                        2
                    )
                )
            )

            //For Pitagoras
            val elevationDiff = abs(segment.points[i].elevation - elevationPrevious) / 1000//km

            //Distance with elevation difference
            val realDistance= sqrt(straightPath.pow(2) + elevationDiff.pow(2))
            path += realDistance

            //Elevation
            if (segment.points[i].elevation > elevationPrevious)
                upAndDown[0] += realDistance.toFloat()
            else
                upAndDown[1] += realDistance.toFloat()
            elevationSum += segment.points[i].elevation

            //Trail attributes
            //Bounds
            bounds.minLatitude= min(bounds.minLatitude,segment.points[i].latitude)
            bounds.maxLatitude= max(bounds.maxLatitude,segment.points[i].latitude)
            bounds.minLongitude= min(bounds.minLongitude,segment.points[i].longitude)
            bounds.maxLongitude= min(bounds.maxLongitude,segment.points[i].longitude)
            //Length
            length+=realDistance
            //Update previous elevation
            elevationPrevious = segment.points[i].elevation
        }
        //Mean elevation
        elevationSum /= segment.points.size
        return TrailSegment(segment.id, elevationSum,
            upAndDown[0], upAndDown[1], segment.points[0].time,segment.points.last().time, path)
    }

    val segments=trail.segments.map { calculateSegmentInfo(it) }.toMutableList()

    return Trail(
        trail.id,
        trail.name,
        length,
        bounds,
        segments.first().timeStart,
        segments.last().timeEnd,
        trail.waypoints,
        segments
    )
}