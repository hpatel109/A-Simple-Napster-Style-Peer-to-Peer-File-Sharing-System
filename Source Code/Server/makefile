JFLAGS = -g
JC = javac
RM = rm -f

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	 Info_File.java\
	method.java\
	 SThread.java\
	 Server.java


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class