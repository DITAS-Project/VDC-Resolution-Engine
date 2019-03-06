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

import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.util.Pair;
import org.json.JSONArray;
import com.ditas.resolutionengine.Configurations.ElasticSearchConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mashape.unirest.http.*;
import com.ditas.resolutionengine.Entities.Requirements;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;


@Service
public class SimilarityRatingService {

    @Autowired
    ElasticSearchConfig config;

    /**
     * Adds the "ranking" field to the blueprints, based on the blueprints' popularity and the similarity of the requirements file.
     * @param requirements The requirements on which to base our ranking.
     * @param blueprints The list of blueprints to look from.
     * @return The list of blueprints with the added field of "ranking".
     */
    public String addRanking(JSONObject requirements, String blueprints) {
       /* PurchaseHandlerService pc = new PurchaseHandlerService(null);
        try {
            pc.clearPurchases();
            pc.pushRandom(50);
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            JSONArray blueprintArray = new JSONArray(blueprints);
            HashMap<String, Pair<Float,Integer>> ratings = new HashMap<String, Pair<Float,Integer>>();
            String blueprintIdList = "[\"";
            for (int i = 0; i < blueprintArray.length(); i++) {
                blueprintIdList+= blueprintArray.getJSONObject(i).getJSONObject("blueprint").getString("_id")+"\",\"";
            }
            blueprintIdList = blueprintIdList.substring(0,blueprintIdList.length()-2)+"]";;
            Pair<String,String> vector = this.createVector(requirements);

            String reqBody = "{\"query\": " +
                    "{\"function_score\": {" +
                    "\"query\": { \"filtered\": { \"filter\": { \"terms\":{" +
                    "\"blueprintID\": "+blueprintIdList+"}}}},"+
                    "\"functions\": [" +
                    "{\"script_score\" : {\"params\":{" +
                    "\"vector\": " + vector.getValue() + "," +
                    "\"field_labels\":" + vector.getKey() + "}," +
                    //"\"script\" : \"int distance(str1,str2){dist=new int[str1.size()+1][str2.size()+1]; (0..str1.size()).each{dist[it][0]=it;}; (0..str2.size()).each{dist[0][it]=it;}; (1..str1.size()).each{i->(1..str2.size()).each{j->dist[i][j]=[dist[i-1][j]+1,dist[i][j-1]+1,dist[i-1][j-1]+((str1[i-1]==str2[j-1])?0:1)].min();};}; return dist[str1.size()][str2.size()];}; vector_B=[]; for(i=0;i<field_labels.size();i++){path = []; splits = ('requirements.'+field_labels[i]).split(/\\\\./); for(j=0;j<splits.size();j++){cur = splits[j]; if(splits[j].isInteger()){cur = splits[j] as Integer;}; path.add(cur);}; cur2=path.inject( _source ) { obj, prop -> obj != null ? obj[prop] : obj; }; if(cur2 instanceof String){cur2=distance(vector[i],cur2);}; vector_B.add(cur2);}; dotProduct=0.0; normA=0.0; normB=0.0; for(i=0;i<vector.size()-1;i++){cur=vector[i]; if(cur instanceof String){cur=distance(vector[i],vector[i]);}; if(vector_B[i] != null){ dotProduct+=cur*vector_B[i]; normA+=cur*cur; normB+=vector_B[i]*vector_B[i];};}; if((Math.sqrt(normA)*Math.sqrt(normB)) > 0){ return (dotProduct/(Math.sqrt(normA)*Math.sqrt(normB)))*_source['score'];};else{return 0.0;};\"}" +
                    "\"script\":\"int distance(str1,str2){dist=new int[str1.size()+1][str2.size()+1]; (0..str1.size()).each{dist[it][0]=it;}; (0..str2.size()).each{dist[0][it]=it;}; (1..str1.size()).each{i->(1..str2.size()).each{j->dist[i][j]=[dist[i-1][j]+1,dist[i][j-1]+1,dist[i-1][j-1]+((str1[i-1]==str2[j-1])?0:1)].min();};}; return dist[str1.size()][str2.size()]; }; def drop(obj,index){ new_obj=[]; for(dr=0;dr<obj.size();dr++){if(dr!=index){new_obj.add(obj[dr]); }; }; return new_obj; }; def getProperty(obj,prop){new_obj = obj; if((obj != null) && (prop.size() > 0)){if(prop[0] instanceof Integer){for(elems=0;elems<obj.size();elems++){new_obj = getProperty(obj[elems],drop(prop,0)); if(new_obj!=null){break; }; }; }else{new_obj = getProperty(obj[prop[0]],drop(prop,0)); }; }; return new_obj; }; vector_B=[]; for(i=0;i<field_labels.size();i++){path = []; splits = ('requirements.'+field_labels[i]).split(/\\\\./); for(j=0;j<splits.size();j++){cur = splits[j]; if(splits[j].isInteger()){cur = splits[j] as Integer; }; path.add(cur); }; cur2=getProperty(_source,path); if(cur2 instanceof String){cur2=distance(vector[i],cur2); }; vector_B.add(cur2); }; dotProduct=0.0; normA=0.0; normB=0.0; for(i=0;i<vector.size()-1;i++){cur=vector[i]; if(cur instanceof String){cur=distance(vector[i],vector[i]); }; if(vector_B[i]){dotProduct+=cur*vector_B[i]; normA+=cur*cur; normB+=vector_B[i]*vector_B[i]; }; else{continue; }; }; if((Math.sqrt(normA)*Math.sqrt(normB)) > 0){return (dotProduct/(Math.sqrt(normA)*Math.sqrt(normB)))*_source['score']; }; else{return 0.0; };\"}"+
                    "}],\"boost_mode\": \"replace\"}},\"sort\" : [\"_score\"]}";
            HttpResponse<String> response = Unirest.post("http://31.171.247.162:50014/ditas/purchaseinfo/_search")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic cHVibGljVXNlcjpSZXNvbHV0aW9u")
                    .header("cache-control", "no-cache")
                    .header("Postman-Token", "5f58f7f0-a1f0-426b-92f1-1fd875e2e355")
                    .header("Method","GET")
                    .body(reqBody)
                    .asString();

            if (response.getCode() == 200) {
                String bodys = response.getBody();
                JSONArray hits = (new JSONObject(bodys)).getJSONObject("hits").getJSONArray("hits");
                for (int i =0;i<hits.length();i++){
                    JSONObject hit = hits.getJSONObject(i);
                    JSONObject source = hit.getJSONObject("_source");
                    double score = hit.getDouble("_score");
                    String bId = source.getString("blueprintID");
                    ratings.merge(bId, new Pair<Float,Integer>((float) score,1), (v1, v2) -> new Pair<Float,Integer>(v1.getKey()+v2.getKey(),v1.getValue()+v2.getValue()));
                }
            }
            else{
                System.out.println(response.getBody());
            }

            for(int i=0;i<blueprintArray.length();i++) {
                JSONObject obj = blueprintArray.getJSONObject(i);
                String id = obj.getJSONObject("blueprint").getString("_id");
                if (ratings.containsKey(id)) {
                    obj.put("userRating", ratings.get(id).getKey() / (float) ratings.get(id).getValue());
                    blueprintArray.put(i, obj);
                }
            }

            return blueprintArray.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Creates the pair of field names and field values to serve as the base vector in the similarity scoring.
     * @param requirements The basis requirements.
     * @return the vector in pair of strings format.
     */
    private Pair<String,String> createVector(JSONObject requirements){
        JSONObject attrs = requirements.getJSONObject("attributes");
        //JSONArray security = attrs.getJSONArray("security");
        //JSONArray privacy = attrs.getJSONArray("privacy");
        JSONArray dataUtility = attrs.getJSONArray("dataUtility");
        ArrayList<Pair<String,Object>> paths = new ArrayList<Pair<String,Object>>();
        for (int i =0;i<dataUtility.length();i++){
            ArrayList<Pair<String,Object>> inner = getPaths(dataUtility.getJSONObject(i));
            for (int j =0;j<inner.size();j++){
                paths.add(new Pair<String,Object>("dataUtility."+i+"."+inner.get(j).getKey(),inner.get(j).getValue()));
            }
        }
        String names = "[\"";
        String values = "[";
        for (int i =0;i<paths.size();i++){
            names += paths.get(i).getKey()+"\",\"";
            values += paths.get(i).getValue()+",";
        }
        names = names.substring(0,names.length()-2)+"]";
        values = values.substring(0,values.length()-1)+"]";
        return new Pair<String,String>(names,values);
    }

    /**
     * Extracts the paths and values of a JSONObject elements in the expected dot separated format.
     * @param obj The JSONObject containing the values.
     * @return The ArrayList of String - Object Pairs extracted.
     */
    private ArrayList<Pair<String,Object>> getPaths(JSONObject obj){
        ArrayList<Pair<String,Object>> kvs = new ArrayList<Pair<String,Object>>();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext()){
            String key = keys.next();
            if (obj.get(key) instanceof JSONObject) {
                ArrayList<Pair<String,Object>> inner = getPaths((JSONObject) obj.get(key));
                if(inner != null) {
                    for (int i = 0; i < inner.size(); i++) {
                        kvs.add(new Pair<>(key + "." + inner.get(i).getKey(), inner.get(i).getValue()));
                    }
                }
            }else if ((!(obj.get(key) instanceof JSONArray))&&(!(obj.get(key) instanceof String))) {
                kvs.add(new Pair<>(key,obj.get(key)));
            }
        }
        return kvs;
    }
}
