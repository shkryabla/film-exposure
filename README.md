# Film Exposure

Плёночный экспонометр для Android. Олдскул: никаких авторежимов и рекомендаций —
только цифры (APEX-система), решение принимает фотограф.

## Возможности

- Экспонометр по превью камеры телефона (замер до 3 точек: тень / средний тон / свет)
- Расчёт пар диафрагма/выдержка под текущий риг (камера + объектив)
- Расчёт ГРИП и гиперфокального расстояния в реальном времени
- Посадка экспозиции: по теням / по свету / по балансу
- Справочники камер, объективов и плёнок (советская и зарубежная техника)
- Пользовательские риги и custom-профили техники
- Учёт закона Шварцшильда (взаимозаместимость) для длинных выдержек
- ЧБ-режим просмотра превью
- Полностью офлайн, без рекламы и аналитики

## Скриншоты

_(placeholder)_

## Требования

- JDK 17
- Android SDK 34
- Android 8.0+ (minSdk 26) на устройстве

## Сборка

### Локально

```bash
gradle assembleDebug
```

APK: `app/build/outputs/apk/debug/`

### Через GitHub Actions

Пуш в `main` → автоматическая сборка debug APK, артефакт доступен во вкладке Actions.

## Подпись release APK

1. Сгенерировать keystore:
   ```bash
   keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```
2. Закодировать в Base64:
   ```bash
   base64 -w0 release.jks > keystore.base64.txt
   ```
3. Добавить в GitHub Secrets репозитория:
   - `KEYSTORE_BASE64` — содержимое keystore.base64.txt
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
4. Запушить тег `v1.0.0` → запустится release workflow, соберётся подписанный APK и создастся GitHub Release.

## Лицензия

MIT — см. [LICENSE](LICENSE).

## Автор

shkryabla@gmail.com
