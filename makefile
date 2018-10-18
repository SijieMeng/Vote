
JAVAC = $(shell which javac)
JAVA = $(shell which java)
all:
	$(JAVAC) server/*.java client/*.java
clean:
	rm *.class
