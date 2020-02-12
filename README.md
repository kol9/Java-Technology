## Домашнее задание 1. Обход файлов
1. Разработайте класс Walk, осуществляющий подсчет хеш-сумм файлов.
    * Формат запуска:  
  `java Walk <входной файл> <выходной файл>`
    * Входной файл содержит список файлов, которые требуется обойти.
    * Выходной файл должен содержать по одной строке для каждого файла. Формат строки:  
  `<шестнадцатеричная хеш-сумма> <путь к файлу>`
    * Для подсчета хеш-суммы используйте алгоритм FNV.
    * Если при чтении файла возникают ошибки, укажите в качестве его хеш-суммы 00000000.
    * Кодировка входного и выходного файлов — UTF-8.
    * Размеры файлов могут превышать размер оперативной памяти.
    * Пример  
  _Входной файл_
  
                        `java/info/kgeorgiy/java/advanced/walk/samples/1`
                        `java/info/kgeorgiy/java/advanced/walk/samples/12`
                        `java/info/kgeorgiy/java/advanced/walk/samples/123`
                        `java/info/kgeorgiy/java/advanced/walk/samples/1234`
                        `java/info/kgeorgiy/java/advanced/walk/samples/1`
                        `java/info/kgeorgiy/java/advanced/walk/samples/binary`
                        `java/info/kgeorgiy/java/advanced/walk/samples/no-such-file`
  _Выходной файл_
  
                        `050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1`
                        `2076af58 java/info/kgeorgiy/java/advanced/walk/samples/12`
                        `72d607bb java/info/kgeorgiy/java/advanced/walk/samples/123`
                        `81ee2b55 java/info/kgeorgiy/java/advanced/walk/samples/1234`
                        `050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1`
                        `8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary`
                        `00000000 java/info/kgeorgiy/java/advanced/walk/samples/no-such-file`  
                    
2. Усложненная версия:
    * Разработайте класс RecursiveWalk, осуществляющий подсчет хеш-сумм файлов в директориях
    * Входной файл содержит список файлов и директорий, которые требуется обойти. Обход директорий осуществляется рекурсивно.
    * Пример  
  Входной файл  
  
                        ` java/info/kgeorgiy/java/advanced/walk/samples/binary
                        java/info/kgeorgiy/java/advanced/walk/samples`    
  Выходной файл  
  
                        `8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary
                        050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1
                        2076af58 java/info/kgeorgiy/java/advanced/walk/samples/12
                        72d607bb java/info/kgeorgiy/java/advanced/walk/samples/123
                        81ee2b55 java/info/kgeorgiy/java/advanced/walk/samples/1234
                        8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary`  
                    
3. При выполнении задания следует обратить внимание на:
    * Дизайн и обработку исключений, диагностику ошибок.
    * Программа должна корректно завершаться даже в случае ошибки.
    * Корректная работа с вводом-выводом.
    * Отсутствие утечки ресурсов.
4. Требования к оформлению задания.
    * Проверяется исходный код задания.
    
    
    Для того, чтобы протестировать программу:

 * Скачайте
    * тесты
        * [info.kgeorgiy.java.advanced.base.jar](artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [info.kgeorgiy.java.advanced.walk.jar](artifacts/info.kgeorgiy.java.advanced.walk.jar)
    * и библиотеки к ним:
        * [junit-4.11.jar](lib/junit-4.11.jar)
        * [hamcrest-core-1.3.jar](lib/hamcrest-core-1.3.jar)
 * Откомпилируйте решение домашнего задания
 * Протестируйте домашнее задание
    * Текущая директория должна:
       * содержать все скачанные `.jar` файлы;
       * содержать скомпилированное решение;
       * __не__ содержать скомпилированные самостоятельно тесты.
    * простой вариант:
        ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>```
    * сложный вариант:
        ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk RecursiveWalk <полное имя класса>```

Исходный код тестов:

 * [простой вариант](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/WalkTest.java)
 * [сложный вариант](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)
