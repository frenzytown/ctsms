#!/bin/bash
CTSMS_PROPERTIES=/ctsms/properties
CTSMS_JAVA=/ctsms/java
export JDK_JAVA_OPTIONS="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED"
#VERSION=$(grep -oP '<application.version>\K[^<]+' /home/runner/work/ctsms/ctsms/pom.xml)
#cat /var/lib/tomcat9/logs/catalina.out
BRANCH=${GITHUB_HEAD_REF:-${GITHUB_REF#refs/heads/}}
#COMMIT=$(git log --format=%B -n 1 "$GITHUB_SHA" | sed -e 's/merge \([a-z0-9]\+\) into [a-z0-9]\+/\1/gi')
MESSAGE=$(git log --format=%B -n 1 "$COMMIT")
mvn surefire:test -DCTSMS_PROPERTIES="$CTSMS_PROPERTIES" -DCTSMS_JAVA="$CTSMS_JAVA" -Dtest=$1 -DskipTests=false -Dwebdriver.chrome.driver="/usr/bin/chromedriver" -Dctsms.test.baseurl="http://localhost:8080" -Dctsms.test.directory="/home/runner/work/ctsms/testoutput" -DsurefireReportsDirectory="/home/runner/work/ctsms/testoutput" -Dctsms.test.windowsize="1560,900" -Dctsms.test.name="$2" -Dctsms.test.emailrecipients="$3" --no-transfer-progress -Dgithub.workflow.url=https://github.com/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID -Dctsms.test.version=$VERSION -Dgit.branch=$BRANCH -Dgit.commit=$COMMIT -Dgit.message="$MESSAGE" -Dftp.host="$FTP_HOST" -Dftp.username="$FTP_USERNAME" -Dftp.password="$FTP_PASSWORD" -Dftp.path="$FTP_PATH" -Dctsms.test.resourcepath="/home/runner/work/ctsms/ctsms/core/src/test/resources"
#sudo $JAVA_HOME/bin/java -Dfile.encoding=Cp1252 -Dwebdriver.chrome.driver=/usr/bin/chromedriver -classpath /home/runner/work/ctsms/ctsms/core/target/test-classes/:/home/runner/work/ctsms/ctsms/selenium-server-standalone-4.4.0.jar $*
#javac -classpath /home/runner/work/ctsms/ctsms/selenium-server-standalone-3.141.59.jar /home/runner/work/ctsms/ctsms/core/src/test/java/org/phoenixctms/ctsms/selenium/$1.java
#sudo java -Dwebdriver.chrome.driver=/usr/bin/chromedriver -classpath /home/runner/work/ctsms/ctsms/selenium-server-standalone-3.141.59.jar:/home/runner/work/ctsms/ctsms/core/src/test/java/ org.phoenixctms.ctsms.selenium.$1
#sudo journalctl --unit tomcat10.service -p err
