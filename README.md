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

## Components

**Content Based Resolution Engine**
The Content Based Resolution Engine is a component that filters the abstract VDC blueprints based on the content they provide, in order to satisfy the functional requirements expressed by the application designer. The component relies on ElasticSearch, which is one of the leading solutions for content-based search.

**Semantic Manager**
The Semantic Manager enhances the content-based resolution in order to return more accurate (enriched) results. This means that more blueprints that the application designer might be interested in will be made available, exploiting the class hierarchy that the ontologies provide. The component uses a revised scoring algorithm to rank the matched blueprints, thus making the DITAS marketplace more efficient, fair and unbiased, regarding the content of the data that a VDC blueprint offers. The Semantic Manager can be extended to any domain, since the corresponding Domain-Specific Ontology (DSO) is the only input that it needs. This component also trains the data administrators at using best practices in the process of filling the description and the method tags parts of the blueprint, in order to make it more discoverable but also with high ranking. This creates a more uniform marketplace by motivating the data administrators to follow the best practices and guidelines. The Semantic Manager is better addressing the requirement T3.10, greatly enhancing the accuracy of the results that the Resolution Engine provides. More information about the requirements can be found in [deliverable D1.2](https://www.ditas-project.eu/wp-content/uploads/2019/01/DITAS-D1.2-Final-Archictecture-and-Validation-1.0-3.pdf).

**Data Utility Resolution Engine**
The Data Utility Resolution Engine (DURE) is described in detail in its [github page](https://github.com/DITAS-Project/data-utility-resolution-engine)

**Privacy and Security Evaluator**
The Privacy and Security Evaluator (PSE) is described in detail in its [github page](https://github.com/DITAS-Project/PrivacySecurityEvaluator)

**Recommendation**
The Recommendation component is providing a crucial functionality to the DITAS Marketplace through the Resolution Engine. It is ranking the blueprints that pass from the DURE and the content based resolution using data from previous purchases of the same blueprints. Each individual that buys a blueprint from the marketplace can submit a rating. This rating is weighted against the similarity of the user requirements of a user trying to make a purchase and the user requirements of the user that made the past purchase. Then these weighted ratings from all of the past purchases are aggregated per blueprint which results to the feedback score of that blueprint. This score is used to provide a second layer ranking to the results that are presented in the marketplace, providing valuable information about how the blueprints are perceived by their users, thus producing more user centric recommendations rather than the technical filtering and raking that the other components produce.
