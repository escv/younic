# younic [![Build Status](https://travis-ci.org/escv/younic.svg?branch=master)](https://travis-ci.org/escv/younic)

Lightweight Web Content Management System without a Database and an Administration Backend

The central idea is to use a File System based Content Repository. Folders represent pages consisting of documents that are converted and put into a context.
Currently, younic only supports plain text and html documents which are put to context. Later, different documents such as .csv, .xml, .docx or .md can be placed to folder.
Appropriate resource converters are able to interpret these files and their content is available inside templates to.

The page context will be provided to a thymeleaf template engine and renders a html webpage. Certain rules allow to overwrite context variables with documents having the same name deaper inside the folder structure (matching algorithm).

The rendering is based on a main template and folders define a specific templated used for rendering their context documents. Those specific template output will be placed to the main template.

Younic is based on a OSGi environment (Felix) so it can be extend by new bundles easily.


### Get started (Developer)

* clone the repository (or a fork) https://github.com/escv/younic-sample next to this project
* duplicate /net.younic.core.dispatcher/launch.bndrun to a personal one and add it to gitignore
** modify the launch bndrun (your copy) config runproperties to fit your environment
** to not modify logging config - create a /var/log/younic.log file add give appropriate permissions for your user account
* Use eclipse bndtools (tested with latest version 4.1 and eclipse photon)
* Open your bndrun file (step 2) and click on either "Run OSGi" or "Debug OSGi"

If you prefer to launch younic as a container, please use the grade build target:
```
gradle clean compileJava dist docker dockerRun
```

run standalone as jar:
```
./gradlew startServerStandalone
```

### Credits:
The sample web page is based on: http://demo.themewagon.com/preview/bootstrap-4-classified-website-template
The hero image was taken from https://pixabay.com/en/fog-forest-mountain-world-clouds-1535201/
