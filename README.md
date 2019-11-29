# ![younic](https://github.com/escv/younic/raw/master/cnf/younic-logo.png) [![Build Status](https://travis-ci.org/escv/younic.svg?branch=master)](https://travis-ci.org/escv/younic)

Lightweight Web Content Management System without a Database and an Administration Backend

The central idea is to use a File System based Content Repository. Folders represent pages consisting of documents that are converted and put into a context map.
Currently, younic supports plain text, html, csv, xml, docx (Word) or md (Markdown) documents which are put to the context map by their filename. File more deeper in the folder hierachy replace more general files having the same name (matching algorithm).
Appropriate resource converters are able to interpret these files and their content is available inside templates to.

The page context map will be provided to a thymeleaf template engine that renders a html webpage. The rendering is based on a main template and folders define a specific templated used for rendering their context documents. Those specific template output will be placed to the main template.
Younic is based on a OSGi environment (Felix) so it can be extend by new bundles easily.

## Cloud Native
The CMS was designed for typical cloud requirements. It is using a distributed version controll system (Git) to synchronize the data between CMS nodes. If there are newer versions of pages, templates and content available, all Younic nodes get those changes via GIT synchronization (Younic does not use any databases). Therewith typical CMS processes can be mapped to git concept:
* Git Branches are used for Staging
* Pull Request are used for (simple) Workflows and Publish-Management
* Git Version are used for Asset Version Histries (and rollback)

Younic offers ready to use images and script for Docker and Kubernetes environmets.

### Get started (Developer)

* younic and Felix OSGi framework currently only support Java8
* clone the repository (or a fork) https://github.com/escv/younic-sample next to this project
* duplicate /net.younic.core.dispatcher/launch.bndrun to a personal one (launch.{name}.bdnrun example launch.aalbert.bndrun) and add it to gitignore
* modify the launch.{name}.bndrun (your copy) config -runproperties to fit your environment
* to not modify logging config - create a /var/log/younic.log file add give appropriate permissions for your user account

## Using Eclipse + bndtools
* Use eclipse bndtools (tested with latest version 4.1 and eclipse photon)
* Open your bndrun file (step 2) and click on either "Run OSGi" or "Debug OSGi"

## Using IntelliJ
* Download the Amdatu Plugin at: https://plugins.jetbrains.com/plugin/10639-amdatu and install it in IDEA
* Start IDEA and Select  File -> New -> Project from Existing Sources... -> Select the younic root Folder  (proceed with default selections)
* Launch it with: Run -> Edit Configurations... -> Click the [+] -> BND OSGi -> Run Launcher -> At the right form: Choose your launch.{name}.bndrun Descriptor -> Apply Changes and run it

If you prefer to launch younic as a container, please use the grade build target:
```
gradle clean compileJava dist docker dockerRun
```

Or from Docker Hub:
```
docker run -e "YOUNIC_CMS_ROOT_GIT=https://github.com/escv/younic-sample.git" -p 8080:8080 escv/youni
```

run standalone as jar:
```
./gradlew startServerStandalone
```

## Cloud Deployments
Younic also supports a kubernetes cluster deployment. To do so, run the command:
```
kubectl apply -f https://raw.githubusercontent.com/escv/younic/develop/container/kube-younic.yaml
```
You can find the git clone URL for the CMS root in a config map. Just replace this URL with your repository and restart your deployment.
To fetch the service port of the running younic service, enter: 
```
kubectl describe service younic-service | grep NodePort
```


### Credits:
The sample web page is based on: http://demo.themewagon.com/preview/bootstrap-4-classified-website-template
The hero image was taken from https://pixabay.com/en/fog-forest-mountain-world-clouds-1535201/
