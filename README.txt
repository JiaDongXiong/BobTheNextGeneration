The archive is made of two subproject:
    - The game written in java, can be imported via gradle IDEs. You then need to set the working directory to the resource folder in the IDE so that it finds the resources.
	In order to run it, type: ./gradlew desktop:run
	A makefile packages it a jar and an exe and deploys it online
	To run the tests type: ./gradlew test:run
    - The website is HTML/JS/CSS combination, just double click on index.html to display it