# Homework 4 + 5

## Студент

- **ФИО:** (указать)
- **Группа:** (указать)

## Домашнее задание 5 — тестирование

### Юнит-тесты (JVM, `test/`)

**Всего: 9**

| Файл | Кол-во | Что проверяется |
|------|--------|-----------------|
| `CountryDtoMappingTest` | 2 | Преобразование DTO → доменная модель (Retrofit/модели) |
| `CountriesListViewModelTest` | 7 | `mutableStateOf`, `viewModelScope`, сценарии списка/поиска/деталей (Loading / Error / Empty / Success), повтор после ошибки, debounce поиска |

### Интеграционные тесты (`androidTest/`)

**Всего: 4**

| Файл | Кол-во | Тип |
|------|--------|-----|
| `DefaultCountriesRepositoryIntegrationTest` | 3 | Data: `DefaultCountriesRepository` + Room (in-memory) + подмена API; без дублей в избранном; чтение после записи; маппинг из «API» |
| `CountriesNavigationIntegrationTest` | 1 | UI: список → клик → детали с нужным `code` (Navigation Compose) |

### Нетривиальные сценарии (по требованиям ДЗ)

- Последовательность **Loading → Success** при загрузке списка.
- **Пустой поиск** даёт `Empty`, а не `Success(emptyList())`.
- **Retry / повторная загрузка** после ошибки (`refresh`, повторный `loadCountryByCode`).
- **Debounce поиска**: после двух быстрых вызовов в репозиторий уходит только последний запрос.
- **Детали по id**: `loadCountryByCode` открывает данные для запрошенного кода.
- **Room**: повторное `addFavourite` не создаёт вторую строку; поля совпадают после чтения.
- **UI**: переход на экран деталей с корректным идентификатором страны.

### Flow / StateFlow

В проекте состояние экранов через **`mutableStateOf`**, не через Flow — дополнительные тесты на эмиссии Flow по методичке не требуются.

### Запуск

```text
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest   # нужен эмулятор или устройство
```

В `gradle.properties` задано `org.gradle.java.home` на JBR из Android Studio (`C:/Program Files/Android/...`), чтобы Gradle не подхватывал старую Java 8 из `PATH`. Если Studio установлена в другом месте — поправьте путь или удалите строку и выставьте **Gradle JDK** в настройках Android Studio (JDK 11+).
