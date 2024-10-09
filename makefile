Main.class: Main.java 
	javac -g Main.java 

run: Main.class
	java Main

clean:
	rm *.class

debug: Main.jar
	jdb Main.jar