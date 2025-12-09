# Script để chạy ứng dụng với Maven và JVM arguments
$env:MAVEN_OPTS = "-Dorg.apache.tomcat.util.http.fileupload.FileCountLimit=10000 -Dorg.apache.tomcat.util.http.fileupload.FileSizeThreshold=0"
Write-Host "Đã set MAVEN_OPTS với FileCountLimit=10000"
Write-Host "Đang chạy ứng dụng..."
mvn spring-boot:run

