# Szlaki
Aplikacja typu lista-szczegóły z animacją i elementami biblioteki wsparcia wzornictwa

▪ Aplikacja została stworzona za pomocą Jetpack Compose 

▪ Do nawigacji zostały wykorzystane własnoręcznie napisane nawigatory opierające się na interfejsach Screen (interfejs obsługiwany przez Navigator) oraz Tab (interfejs obsługiwany przez TabNavigator) 

▪ Baza danych stworzona za pomocą Room i Dao 

▪ Zamiast trybu tabletowego aplikacja obsługuje składany telefon - wykrywany jest stan zawiasu a nie rozmiar ekranu tak więc na tablecie będzie widok „pojedynczy” 

▪ (Obsługa ta jest możliwa tylko za pomocą dodatkowego wątku w tle sprawdzającego zmiany stanu zawiasu – okazuje się iż obrót urządzenia jest sprawą poważniejszą niż zmiana ekranu na którym wyświetla się treść jako że nie powoduje ona ponownego tworzenia aktywności (wywołania funkcji onCreate)) 

▪ Aplikacja korzysta z jednej aktywności (zmiana ekranów następuje poprzez zmianę 
wyświetlanej funkcji) i jednego ViewModel

