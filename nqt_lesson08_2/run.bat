@echo off
echo Starting application with increased file upload limit...
java -Dorg.apache.tomcat.util.http.fileupload.FileCountLimit=10000 -Dorg.apache.tomcat.util.http.fileupload.FileSizeThreshold=0 -jar target\nqt_lesson08_2-0.0.1-SNAPSHOT.jar

