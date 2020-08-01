#KGEORGIY_PATH=../../../../../../../java-advanced-2020
KGEORGIY_PATH=../../../../..
javadoc \
    -link https://docs.oracle.com/en/java/javase/11/docs/api/ \
    -html5 -private \
    -d _javadoc \
    ./*.java \
    "$KGEORGIY_PATH"/info/kgeorgiy/java/advanced/implementor/Impler.java \
    "$KGEORGIY_PATH"/info/kgeorgiy/java/advanced/implementor/JarImpler.java \
    "$KGEORGIY_PATH"/info/kgeorgiy/java/advanced/implementor/ImplerException.java