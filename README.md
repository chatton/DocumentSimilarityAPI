This application consists of a DocumentSimilarityAPI and a Runner class.

In order to run the application, navigate to the directory that oop.jar is in, and run the command
```bash
java –cp ./oop.jar ie.gmit.sw.Runner
```
or with the command

	java -jar ./oop.jar

to launch the application. You must be sure to have java 1.8+ installed.

This jar contains all additional required libraries (Guava & Jsoup)

It is possible compare Documents created from files, urls with HTML tags, and plain text.
The Jaacard Index of the provided documents is computed and reported to the user.



JavaDocs were generated for this project using the command

	javadoc -sourcepath ./src -d ./docs -subpackages . -noqualifier all -private

I chose to add the -private flag as many important methods are private, and most of the interfaces have
a single public method.

I added the noqualifier flag to avoid clutter with standard library names.



#Design Descisions.

Immutibilitiy - I chose to make as many classes as possible immutable. And used Guavas
immutable collectiosn packages extensively.

I chose to use Futures instead of Runnables/Threads to make it easier to reason about and debug the code
during development.

I aimed to make the classes depend on interfaces (Document, Shinglizer, SimilarityIndex) instead of any concrete
implementations of these classes. While the Shinglizer interface may not be required as there is only a single
implementation, this would easily allow additional implementations to be written with not change to the JaacardIndex
class.


#Extra features.

1. You can create URLDocuments by choosing the relevant option in the menu. This utilzes the Jsoup library (https://jsoup.org/)
to construct a URLDocument which implements the Document interface and so these Documents can be compared with a SimilarityIndex.

2. CachingSimilarityIndex - this is an implementation of the SimilairtyIndex interface that caches any results
that the object has already computed. This object requires a SimilarityIndex instance to be passed into its
constructor. It will cache the results computed via that implementatation.

3. The SimilairtIndex interface requires a List of Documents, meaning that you can compare more than just 2
documents. (fewer than 2 will results in IllegalArgumentException in current implementations.).

4. PlantUML was used to generate the provided UML diagram. (http://plantuml.com/). See the UML.puml file included.

5. The UI class relies on a "User" implementation. This allows you to automate interaction with the UI class by providing a series of pre-set instructions in a file instead of manually interacting the program. And saving the results for examination in another file. (more useful for testing rather than from a human user's perspective.)

# UML Design created using PlantUML

![UML](https://github.com/chatton/DocumentSimilarityAPI/blob/master/resources/design.png)
