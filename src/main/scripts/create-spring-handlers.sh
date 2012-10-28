
for jarfile in $(find ~/.m2/repository/org/springframework/*/3.1.0.RELEASE -name "*.jar")
do
    cd ~/scratch
    jar -xvf $jarfile META-INF/spring.handlers
    cat META-INF/spring.handlers >> spring.handlers
    jar -xvf $jarfile META-INF/spring.schemas
    cat META-INF/spring.schemas >> spring.schemas
done
