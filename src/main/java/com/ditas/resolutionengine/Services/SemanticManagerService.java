/*
 * This file is part of VDC-Resolution-Engine.
 * 
 * VDC-Resolution-Engine is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * VDC-Resolution-Engine is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VDC-Resolution-Engine.  
 * If not, see <https://www.gnu.org/licenses/>.
 * 
 * VDC-Resolution-Engine is being developed for the
 * DITAS Project: https://www.ditas-project.eu/
 */
package com.ditas.resolutionengine.Services;

import com.ditas.resolutionengine.Entities.Requirements;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class SemanticManagerService {
    
    public String createSemanticElasticSearchQuery (Requirements requirements) throws IOException {
                
        String descriptionExactSynonym = requirements.getVdcTags();
        String descriptionParentChild = "";
        String descriptionSibling = "";
        ArrayList<String> descriptionSiblingArrayList = new ArrayList<String>();
        
        String methodTagExactSynonym = requirements.getMethodTags();
        String methodTagParentChild = "";
        String methodTagSibling = "";
        ArrayList<String> methodTagSiblingArrayList = new ArrayList<String>();
        
        
        String[] vdcTagsArray = requirements.getVdcTags().trim().split("\\s+");
        ArrayList<String> vdcTagsArrayList = new ArrayList<String>();
        for (int i=0; i<vdcTagsArray.length; i++) {
            vdcTagsArrayList.add(vdcTagsArray[i]);
        }
       
        String[] methodTagsArray = requirements.getMethodTags().trim().split("\\s+");
        ArrayList<String> methodTagsArrayList = new ArrayList<String>();
        for (int i=0; i<methodTagsArray.length; i++) {
            methodTagsArrayList.add(methodTagsArray[i]);
        }
        
        OntModelSpec s = new OntModelSpec(PelletReasonerFactory.THE_SPEC);
        OntDocumentManager dm = OntDocumentManager.getInstance();
        dm.setFileManager(FileManager.get());
        s.setDocumentManager(dm);
        OntModel m = ModelFactory.createOntologyModel(s,null);
        org.springframework.core.io.Resource resource = new ClassPathResource("medical_ontology.owl");
        InputStream resourceInputStream = resource.getInputStream();
        m.read(resourceInputStream,null);
        
        ExtendedIterator<Individual> individuals = m.listIndividuals();
        while (individuals.hasNext()) {
  
            Individual currentIndividual = individuals.next();
            Integer i = currentIndividual.getNameSpace().length();
            
            
            if (vdcTagsArrayList.contains(currentIndividual.toString().substring(i))) {
                
                ExtendedIterator<? extends Resource> listSameAs = currentIndividual.listSameAs();
                while (listSameAs.hasNext()) {
                    
                    Resource sameInd = listSameAs.next();
                    if (!sameInd.equals(currentIndividual)) {
                        Integer j = sameInd.getNameSpace().length();
                        descriptionExactSynonym += sameInd.toString().substring(j)+" ";
                    }    
                }
      
            
                ExtendedIterator<ObjectProperty> props = m.listObjectProperties();
                while (props.hasNext()) {
            
                    StmtIterator t = currentIndividual.listProperties(props.next());
            
                    while (t.hasNext()) {
                
                    RDFNode node = t.next().getObject();
                    Integer k = node.asResource().getNameSpace().length();
                    descriptionParentChild += node.asResource().toString().substring(k)+" ";
                    }
                }
            
            
                ExtendedIterator<OntClass> classes = currentIndividual.listOntClasses(true);
                while (classes.hasNext()) {
                
                    ExtendedIterator<? extends OntResource> listInstances = classes.next().listInstances();
                    while (listInstances.hasNext()) {
                    
                        OntResource next = listInstances.next();
                        if (!next.equals(currentIndividual) && !next.isSameAs(currentIndividual)) {
                            Integer l = next.getNameSpace().length();
                            descriptionSiblingArrayList.add(next.toString().substring(l));
                        }    
                    }
                }
            }
            
            
            
            if (methodTagsArrayList.contains(currentIndividual.toString().substring(i))) {
                
                ExtendedIterator<? extends Resource> listSameAs = currentIndividual.listSameAs();
                while (listSameAs.hasNext()) {
                    
                    Resource sameInd = listSameAs.next();
                    if (!sameInd.equals(currentIndividual)) {
                        Integer j = sameInd.getNameSpace().length();
                        methodTagExactSynonym += sameInd.toString().substring(j)+" ";
                    }    
                }
      
            
                ExtendedIterator<ObjectProperty> props = m.listObjectProperties();
                while (props.hasNext()) {
            
                    StmtIterator t = currentIndividual.listProperties(props.next());
            
                    while (t.hasNext()) {
                
                    RDFNode node = t.next().getObject();
                    Integer k = node.asResource().getNameSpace().length();
                    methodTagParentChild += node.asResource().toString().substring(k)+" ";
                    }
                }
            
            
                ExtendedIterator<OntClass> classes = currentIndividual.listOntClasses(true);
                while (classes.hasNext()) {
                
                    ExtendedIterator<? extends OntResource> listInstances = classes.next().listInstances();
                    while (listInstances.hasNext()) {
                    
                        OntResource next = listInstances.next();
                        if (!next.equals(currentIndividual) && !next.isSameAs(currentIndividual)) {
                            Integer l = next.getNameSpace().length();
                            methodTagSiblingArrayList.add(next.toString().substring(l));
                        }    
                    }
                }
            }
        }
        
        
        ExtendedIterator<OntClass> listClasses = m.listClasses();
        while (listClasses.hasNext()) {
            
            OntClass currentClass = listClasses.next();
            
            if ((currentClass.hasSuperClass()) && (currentClass.hasSubClass()) && (!(currentClass.getNameSpace() == null))) {
                Integer i = currentClass.getNameSpace().length();
                if (vdcTagsArrayList.contains(currentClass.toString().substring(i))) {
            
                    ExtendedIterator<OntClass> equivalentClasses = currentClass.listEquivalentClasses();
                    while (equivalentClasses.hasNext()) {
                
                        OntClass eqClass = equivalentClasses.next();
                        if (!eqClass.equals(currentClass)) {
                            Integer j = eqClass.getNameSpace().length();
                            descriptionExactSynonym += eqClass.toString().substring(j)+" ";
                        }    
                    }
            
            
                    ExtendedIterator<OntClass> subClasses = currentClass.listSubClasses();
                    while (subClasses.hasNext()) {
                
                        OntClass subClass = subClasses.next();
                        if ((subClass.hasSubClass()) && (!(subClass.getNameSpace() == null))) {
                            Integer k = subClass.getNameSpace().length();
                            descriptionParentChild += subClass.toString().substring(k)+" ";
                        }
                    }
            
            
            
                    ExtendedIterator<OntClass> superClasses = currentClass.listSuperClasses();
                    while (superClasses.hasNext()) {
                
                        OntClass superClass = superClasses.next();
                        if ((superClass.hasSuperClass()) && (!(superClass.getNameSpace() == null))) {
                            Integer l = superClass.getNameSpace().length();
                            descriptionParentChild += superClass.toString().substring(l)+" ";
                        }    
                    }
            
            
                    ExtendedIterator<OntClass> directSuperClasses = currentClass.listSuperClasses(true);
                    while (directSuperClasses.hasNext()) {
                
                        OntClass directSuperClass = directSuperClasses.next();
                        ExtendedIterator<OntClass> directSubClassesOfDirectSuperClass = directSuperClass.listSubClasses(true);
                        while (directSubClassesOfDirectSuperClass.hasNext()) {
                    
                            OntClass directSubClassOfDirectSuperClass = directSubClassesOfDirectSuperClass.next();
                            if (!directSubClassOfDirectSuperClass.equals(currentClass) && !directSubClassOfDirectSuperClass.hasEquivalentClass(currentClass)) {
                                Integer n = directSubClassOfDirectSuperClass.getNameSpace().length();
                                if (!descriptionSiblingArrayList.contains(directSubClassOfDirectSuperClass.toString().substring(n))) {
                                    descriptionSiblingArrayList.add(directSubClassOfDirectSuperClass.toString().substring(n));
                                }
                            }    
                        }
                    }
                }
                
                if (methodTagsArrayList.contains(currentClass.toString().substring(i))) {
            
                    ExtendedIterator<OntClass> equivalentClasses = currentClass.listEquivalentClasses();
                    while (equivalentClasses.hasNext()) {
                
                        OntClass eqClass = equivalentClasses.next();
                        if (!eqClass.equals(currentClass)) {
                            Integer j = eqClass.getNameSpace().length();
                            methodTagExactSynonym += eqClass.toString().substring(j)+" ";
                        }    
                    }
            
            
                    ExtendedIterator<OntClass> subClasses = currentClass.listSubClasses();
                    while (subClasses.hasNext()) {
                
                        OntClass subClass = subClasses.next();
                        if ((subClass.hasSubClass()) && (!(subClass.getNameSpace() == null))) {
                            Integer k = subClass.getNameSpace().length();
                            methodTagParentChild += subClass.toString().substring(k)+" ";
                        }    
                    }
            
            
            
                    ExtendedIterator<OntClass> superClasses = currentClass.listSuperClasses();
                    while (superClasses.hasNext()) {
                
                        OntClass superClass = superClasses.next();
                        if ((superClass.hasSuperClass()) && (!(superClass.getNameSpace() == null))) {
                            Integer l = superClass.getNameSpace().length();
                            methodTagParentChild += superClass.toString().substring(l)+" ";
                        }    
                    }
            
            
                    ExtendedIterator<OntClass> directSuperClasses = currentClass.listSuperClasses(true);
                    while (directSuperClasses.hasNext()) {
                
                        OntClass directSuperClass = directSuperClasses.next();
                        ExtendedIterator<OntClass> directSubClassesOfDirectSuperClass = directSuperClass.listSubClasses(true);
                        while (directSubClassesOfDirectSuperClass.hasNext()) {
                    
                            OntClass directSubClassOfDirectSuperClass = directSubClassesOfDirectSuperClass.next();
                            if (!directSubClassOfDirectSuperClass.equals(currentClass) && !directSubClassOfDirectSuperClass.hasEquivalentClass(currentClass)) {
                                Integer n = directSubClassOfDirectSuperClass.getNameSpace().length();
                                if (!methodTagSiblingArrayList.contains(directSubClassOfDirectSuperClass.toString().substring(n))) {
                                    methodTagSiblingArrayList.add(directSubClassOfDirectSuperClass.toString().substring(n));
                                }
                            }    
                        }
                    }
                }
            }
        }
                               
        
        descriptionExactSynonym = descriptionExactSynonym.trim();
        descriptionParentChild = descriptionParentChild.trim();
        for (int i=0; i<descriptionSiblingArrayList.size(); i++) {
            descriptionSibling += descriptionSiblingArrayList.get(i)+" ";
        }
        descriptionSibling = descriptionSibling.trim();
        
        methodTagExactSynonym = methodTagExactSynonym.trim();
        methodTagParentChild = methodTagParentChild.trim();
        for (int i=0; i<methodTagSiblingArrayList.size(); i++) {
            methodTagSibling += methodTagSiblingArrayList.get(i)+" ";
        }
        methodTagSibling = methodTagSibling.trim();
        
        String semanticQuery = "{ \n" +
        "   \"query\":{ \n" +
        "      \"bool\":{ \n" +
        "         \"must\":{ \n" +
        "            \"bool\":{ \n" +
        "               \"should\":[ \n" +
        "                  { \n" +
        "                     \"function_score\":{ \n" +
        "                        \"query\":{ \n" +
        "                           \"bool\":{ \n" +
        "                              \"must\":{ \n" +
        "                                 \"match_all\":{ \n" +
        "\n" +
        "                                 }\n" +
        "                              },\n" +
        "                              \"filter\":[ \n" +
        "                                 { \n" +
        "                                    \"nested\":{ \n" +
        "                                       \"query\":{ \n" +
        "                                          \"match\":{ \n" +
        "                                             \"tags.tags\":{ \n" +
        "                                                \"query\" : \""+methodTagExactSynonym+"\"\n" +
        "                                             }\n" +
        "                                          }\n" +
        "                                       },\n" +
        "                                       \"path\":\"tags\",\n" +
        "                                       \"inner_hits\":{ \n" +
        "                                          \"size\":10,\n" +
        "                                          \"name\":\"exact_synonym\"\n" +
        "                                       }\n" +
        "                                    }\n" +
        "                                 }\n" +
        "                              ]\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"field_value_factor\":{ \n" +
        "                           \"field\":\"tagsFactor\"\n" +
        "                        },\n" +
        "                        \"boost\":256\n" +
        "                     }\n" +
        "                  },\n" +
        "                  { \n" +
        "                     \"function_score\":{ \n" +
        "                        \"query\":{ \n" +
        "                           \"bool\":{ \n" +
        "                              \"must\":{ \n" +
        "                                 \"match_all\":{ \n" +
        "\n" +
        "                                 }\n" +
        "                              },\n" +
        "                              \"filter\":[ \n" +
        "                                 { \n" +
        "                                    \"nested\":{ \n" +
        "                                       \"query\":{ \n" +
        "                                          \"match\":{ \n" +
        "                                             \"tags.tags\":{ \n" +
        "                                                \"query\" : \""+methodTagParentChild+"\"\n" +
        "                                             }\n" +
        "                                          }\n" +
        "                                       },\n" +
        "                                       \"path\":\"tags\",\n" +
        "                                       \"inner_hits\":{ \n" +
        "                                          \"size\":10,\n" +
        "                                          \"name\":\"parent_child\"\n" +
        "                                       }\n" +
        "                                    }\n" +
        "                                 }\n" +
        "                              ],\n" +
        "                              \"must_not\":{ \n" +
        "                                 \"nested\":{ \n" +
        "                                    \"query\":{ \n" +
        "                                       \"match\":{ \n" +
        "                                          \"tags.tags\":{ \n" +
        "                                             \"query\" : \""+methodTagExactSynonym+"\"\n" +
        "                                          }\n" +
        "                                       }\n" +
        "                                    },\n" +
        "                                    \"path\":\"tags\"\n" +
        "                                 }\n" +
        "                              }\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"field_value_factor\":{ \n" +
        "                           \"field\":\"tagsFactor\"\n" +
        "                        },\n" +
        "                        \"boost\":64\n" +
        "                     }\n" +
        "                  },\n" +
        "                  { \n" +
        "                     \"function_score\":{ \n" +
        "                        \"query\":{ \n" +
        "                           \"bool\":{ \n" +
        "                              \"must\":{ \n" +
        "                                 \"match_all\":{ \n" +
        "\n" +
        "                                 }\n" +
        "                              },\n" +
        "                              \"filter\":[ \n" +
        "                                 { \n" +
        "                                    \"nested\":{ \n" +
        "                                       \"query\":{ \n" +
        "                                          \"match\":{ \n" +
        "                                             \"tags.tags\":{ \n" +
        "                                                \"query\" : \""+methodTagSibling+"\"\n" +
        "                                             }\n" +
        "                                          }\n" +
        "                                       },\n" +
        "                                       \"path\":\"tags\",\n" +
        "                                       \"inner_hits\":{ \n" +
        "                                          \"size\":10,\n" +
        "                                          \"name\":\"sibling\"\n" +
        "                                       }\n" +
        "                                    }\n" +
        "                                 }\n" +
        "                              ],\n" +
        "                              \"must_not\":{ \n" +
        "                                 \"nested\":{ \n" +
        "                                    \"query\":{ \n" +
        "                                       \"match\":{ \n" +
        "                                          \"tags.tags\":{ \n" +
        "                                             \"query\" : \""+methodTagExactSynonym+"\"\n" +
        "                                          }\n" +
        "                                       }\n" +
        "                                    },\n" +
        "                                    \"path\":\"tags\"\n" +
        "                                 }\n" +
        "                              }\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"field_value_factor\":{ \n" +
        "                           \"field\":\"tagsFactor\"\n" +
        "                        },\n" +
        "                        \"boost\":32\n" +
        "                     }\n" +
        "                  }\n" +
        "               ]\n" +
        "            }\n" +
        "         },\n" +
        "         \"should\":[ \n" +
        "            { \n" +
        "               \"function_score\":{ \n" +
        "                  \"query\":{ \n" +
        "                     \"bool\":{ \n" +
        "                        \"must\":{ \n" +
        "                           \"match_all\":{ \n" +
        "\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"filter\":[ \n" +
        "                           { \n" +
        "                              \"match\":{ \n" +
        "                                 \"description\":{ \n" +
        "                                    \"query\" : \""+descriptionExactSynonym+"\"\n" +
        "                                 }\n" +
        "                              }\n" +
        "                           }\n" +
        "                        ]\n" +
        "                     }\n" +
        "                  },\n" +
        "                  \"field_value_factor\":{ \n" +
        "                     \"field\":\"descriptionFactor\"\n" +
        "                  },\n" +
        "                  \"boost\":8\n" +
        "               }\n" +
        "            },\n" +
        "            { \n" +
        "               \"function_score\":{ \n" +
        "                  \"query\":{ \n" +
        "                     \"bool\":{ \n" +
        "                        \"must\":{ \n" +
        "                           \"match_all\":{ \n" +
        "\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"filter\":[ \n" +
        "                           { \n" +
        "                              \"match\":{ \n" +
        "                                 \"description\":{ \n" +
        "                                    \"query\" : \""+descriptionParentChild+"\"\n" +
        "                                 }\n" +
        "                              }\n" +
        "                           }\n" +
        "                        ],\n" +
        "                        \"must_not\":{ \n" +
        "                           \"match\":{ \n" +
        "                              \"description\":{ \n" +
        "                                 \"query\" : \""+descriptionExactSynonym+"\"\n" +
        "                              }\n" +
        "                           }\n" +
        "                        }\n" +
        "                     }\n" +
        "                  },\n" +
        "                  \"field_value_factor\":{ \n" +
        "                     \"field\":\"descriptionFactor\"\n" +
        "                  },\n" +
        "                  \"boost\":2\n" +
        "               }\n" +
        "            },\n" +
        "            { \n" +
        "               \"function_score\":{ \n" +
        "                  \"query\":{ \n" +
        "                     \"bool\":{ \n" +
        "                        \"must\":{ \n" +
        "                           \"match_all\":{ \n" +
        "\n" +
        "                           }\n" +
        "                        },\n" +
        "                        \"filter\":[ \n" +
        "                           { \n" +
        "                              \"match\":{ \n" +
        "                                 \"description\":{ \n" +
        "                                    \"query\" : \""+descriptionSibling+"\"\n" +
        "                                 }\n" +
        "                              }\n" +
        "                           }\n" +
        "                        ],\n" +
        "                        \"must_not\":{ \n" +
        "                           \"match\":{ \n" +
        "                              \"description\":{ \n" +
        "                                 \"query\" : \""+descriptionExactSynonym+"\"\n" +
        "                              }\n" +
        "                           }\n" +
        "                        }\n" +
        "                     }\n" +
        "                  },\n" +
        "                  \"field_value_factor\":{ \n" +
        "                     \"field\":\"descriptionFactor\"\n" +
        "                  },\n" +
        "                  \"boost\":1\n" +
        "               }\n" +
        "            }\n" +
        "         ]\n" +
        "      }\n" +
        "   }\n" +
        "}";
        
        return semanticQuery;
    }   
}