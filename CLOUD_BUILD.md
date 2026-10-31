# Облачная сборка APK

В репозитории уже добавлен workflow:

`.github/workflows/build-apk.yml`

Он:

1. скачивает проект вместе с сабмодулями;
2. устанавливает Java 17, Android SDK 36, NDK 27.2.12479018 и CMake 3.22.1;
3. собирает `:TMessagesProj_App:assembleAfatDebug`;
4. публикует APK как artifact `telegram-mod-debug-apk`.

## Запуск

1. Загрузите репозиторий на GitHub.
2. Откройте вкладку **Actions**.
3. Выберите **Build APK**.
4. Нажмите **Run workflow**.
5. После завершения скачайте APK из блока **Artifacts**.

Для приватного репозитория достаточно обычного `git push`. Секреты для сборки
не требуются: проект содержит конфигурацию debug-сборки и тестовый ключ из
исходного проекта.

`gradlew.bat` также добавлен для локального запуска wrapper на Windows.
