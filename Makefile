JFLAGS = -g
JC = javac#~/../../usr/lib/jvm/java-6-openjdk/bin/javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	DNSreply.java\
        DNSlookup.java \
        Server.java \
        Client.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
