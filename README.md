<!--
This README intends to be a starter template for the Pearson Student Coding Contest. Feel free to add or omit content as needed for your app. The formatting is done using Markdown. These comment sections are simply guides that you can delete.
-->

# "ClassMap"


<!--
The "App Overview" section intends to be a high level description of your app. Think of what you might want to know if considering a purchase in an app store. 
-->

## App Overview

The use of imagery and keywords are two of the strongest techniques for learning a new topic or subject. These two techniques can be used by people with learning disabilities, students learning foreign languages, and just about anyone else struggling with new subjects. The concept of using imagery and keywords is the whole idea behind mind maps. Now imagine, a mind map where students in a particular class could post keywords, images, and other useful items to an ever growing, ever changing community mind map. A mind map that is expanded and shaped by the singular mind of a class! This is the idea behind our application, ClassMap.
ClassMap will consist of a drag and drop interface to allow for a more user friendly experience. To offset the clutter that multiple users will undoubtedly bring to a mind map, ClassMap will feature an "upvote" system to allow the most helpful information to appear more prominent, while hiding "less" helpful information. A mind map can't grow if a student does not understand the keywords they should expand on. Using Pearon's dictionary APIs, we will give meaning to complex keywords that may be difficult for some students to understand, allowing for greater expansion, and in the end, better understanding. In the end, the teacher should have a map of the brain of the class. By observing keywords, images, files, and how they were expanded on, the teacher will be able to see which topics need to be focused on more and hopefully be able to touch on those topics in a more relatable way to the student.

### Project GitHub Repository
[ClassMap GitHub](https://github.com/classmapapp/Student-Coding-Contest-ClassMap)
### Planning

[See this project's Devpost entry page!](http://devpost.com/software/classmap)

### Demo

[Watch a video of the application in action!](https://www.youtube.com/watch?v=ExpJavVUKfE)

### Screenshots

![Login Screenshot](http://www.aaronwmartin.com/images/login.jpg)
![Register Screenshot](http://www.aaronwmartin.com/images/register.jpg)
![Running Screenshot](http://www.aaronwmartin.com/images/running.jpg)


<!--
The "App Details" section intends to explain how your app works. Describe the major components, what APIs were used, and what is missing to make this production ready.
-->

## App Details

ClassMap is a desktop application which consists of a drag and drop interface. Students and teachers can add a variety of node types to the map including, Text Nodes, Image Nodes, and YouTube videos. Right clicking on a node will bring up it's content in a larger window for easier viewing. An upvote system ensures you can always do away with 
nodes you may not be interested in. Once and user has upvoted a node, they have the option to click the home button, bringing up their personalized ClassMap.<p>
You can either log in to our premade accounts or create your own. The log in details for a teacher and student account are below.<br>
Student- username: amartin125971, password: hello<br>
Teacher- username: kumar, password: kumar

### API Usage

 * [LearningStudio API](http://developer.pearson.com/products/learningstudio) - provided discussion topics
 * [Pearson Content API](http://developer.pearson.com/apis/dictionaries) - provided definitions for text nodes

### Scope of Functionality 

Although ClassMap is not technically limited in any aspect of it's functionality, users have experienced odd map glitches that bring down the map. If the map extends too far, the map can break and may remain broke until the trouble node is removed. Other problems exist with our log-in and Classmap loading times. The load times may be a bit excessive as more and more nodes are added. If your window says not responding, simply wait until all the data is loaded and the information will be displayed on the window when it is all loaded.

<!--
The "Prerequisites" section intends to assist someone get started with your source code. They might not be familar with your frameworks or project structure. Help them out by explaining what you already know. 
-->

## Prerequisites
The following prerequisites are essential to be able to use and modify our code. You must have prior knowledge in MySQL to handle the back-end database side, JavaFX, basic knowledge of tree structures, and finally basic trigonometry.

### Build Environment 

 * IntelliJ IDEA
 * [JDK 8u72](https://jdk8.java.net/download.html) - Needed to display YouTube videos

<!--
The "Installation" section intends to assist someone deploy your project themselves. What do they need to configure, package, and distribute?
-->

## Application Deployment
"Run<br>
\ClassMapAppFX\out\artifacts\ClassMapAppFX\ClassMapAppFX.jar
<br>From Student-Coding-Contest-ClassMap repository.

<!--
The "Credit" section intends to highlight your team. Tell who contributed to what parts of the project. Give thanks to mentors that were helpful.
-->

### Team

This project was a collaborative effort. Everyone attends Troy University other than Jamie who attends University of West Florida.

 * [Aaron Martin](mailto: amartin125971@troy.edu) - Creator, programmer
 * [Jamie Davis](mailto: jedwarddavis902@gmail.com) - Programmer
 * [Travis Clinkscales](mailto: tclinkscales@troy.edu) - Programmer, stylist
 * [Soumith Thumma](https://github.com/soumiththumma) - Programmer
 * [John Tran](https://www.facebook.com/john.m.tran.7?fref=ts) - Researcher, Artist

### Other

This project would not have been possible without [Professor Kumar](mailto: skumar@troy.edu). He informed us of the coding contest and acted as mentored during the entire process.

<!--
The "License" section intends to be a license declaration. Checkout choosealicence.com to become familar with different licences. The full license should be included in the LICENSE file, but you can also declare and link to it here.
-->

## License

*[License.txt](https://github.com/classmapapp/Student-Coding-Contest-ClassMap/blob/master/License.txt)
