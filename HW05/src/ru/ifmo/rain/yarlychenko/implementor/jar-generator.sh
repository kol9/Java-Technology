#KGEORGIY_PATH=../../../../../../../java-advanced-2020
KGEORGIY_PATH=../../../../../java-advanced-2020
module=info/kgeorgiy/java/advanced/implementor
cur=ru/ifmo/rain/yarlychenko/implementor
dir="jarfiles"

javac -d "${dir}" -cp "${KGEORGIY_PATH}/artifacts/*" JarImplementor.java Implementor.java
cd ${dir}
echo "Manifest-Version: 1.0
Main-Class: ru.ifmo.rain.yarlychenko.implementor.JarImplementor" > MANIFEST.MF
jar xf ../${KGEORGIY_PATH}/artifacts/info.kgeorgiy.java.advanced.implementor.jar ${module}/Impler.class ${module}/JarImpler.class ${module}/ImplerException.class
jar cfm ../_implementor.jar MANIFEST.MF ${cur}/*.class ${module}/*.class
cd ../
rm -r ${dir}