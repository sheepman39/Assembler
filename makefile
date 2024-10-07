Main.class: Main.java 
	javac -g Main.java 

run: Main.class
	java Main

clean:
	rm *.class *.jar

debug: Main.jar
	jdb Main.jar