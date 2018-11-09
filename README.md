# VDC-Resolution-Engine
This repository contains all the API methods and UI of the resolution engine

## License
This file is part of VDC-Resolution-Engine.

VDC-Resolution-Engine is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as 
published by the Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

VDC-Resolution-Engine is distributed in the hope that it will be 
useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with VDC-Resolution-Engine.  
If not, see <https://www.gnu.org/licenses/>.

VDC-Resolution-Engine is being developed for the
DITAS Project: https://www.ditas-project.eu/

## Functionalities 
* `POST` `/searchBlueprintByReq`
  * **description**: This methods retrieves the blueprints responded by DURE 
  * (indicative) **caller** Resolution Engine
  * **input**: Application requirements JSON file
  * **output**: Blueprints

* `POST` `/searchBlueprintByReq_ESresponse`  
  * **description**: This methods retrieves the results of the ElasticSearch search based on VDC and Method tags 
  * (indicative) **caller** Resolution Engine
  * **input**: Application requirements JSON file
  * **output**: ElasticSearch JSON response

* `POST` `/searchBlueprintByReq_DureRequest`  
  * **description**: This method retrieves the JSON request to DURE built from Resolution Engine 
  * (indicative) **caller** Resolution Engine
  * **input**: Application requirements JSON file
  * **output**: JSON file for DURE request
  
## Language
Java

## Requirements
* Java 8
* Gradle 4.x (build tool)

## Installation
Clone repository

## Execution
* Configuration Files:
	src/main/resources/application.properties
	(configure elasticsearch connection)
* Deploy using embedded tomcat:
	$ gradlew bootRun
* Deploy in standalone application server:
  * Build war file: $ gradlew bootWar
  * Deploy build/libs/Resolution-Engine-0.0.1-SNAPSHOT.war to server
* Run as jar:
  * Build jar file: $ gradlew bootJar
  * $java –jar build/libs/Resolution-Engine-0.0.1-SNAPSHOT.jar
