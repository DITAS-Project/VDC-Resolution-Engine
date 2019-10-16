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

import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ditas.resolutionengine.Entities.Requirements;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;


@Service
public class SimilarityRatingService {

    @Value("${elasticsearch.index}")
    private String EsIndex;

    @Value("${purchase.index}")
    private String PurchIndex;

    @Value("${elasticsearch.auth}")
    private String EsAuth;

    @Value("${elasticsearch.user}")
    private String EsUser;

    @Value("${elasticsearch.pass}")
    private String EsPass;

    @Value("${eshost}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

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
        JSONArray blueprintArray = new JSONArray();
        try {
            if(blueprints != null){
                blueprintArray = new JSONArray(blueprints);
            }else{
                return blueprintArray.toString();
            }
            if(blueprintArray.length() > 0) {
                HashMap<String, Pair<Float, Integer>> ratings = new HashMap<String, Pair<Float, Integer>>();
                String blueprintIdList = "[\"";
                for (int i = 0; i < blueprintArray.length(); i++) {
                    blueprintIdList += blueprintArray.getJSONObject(i).getJSONObject("blueprint").getString("_id") + "\",\"";
                }
                blueprintIdList = blueprintIdList.substring(0, blueprintIdList.length() - 2) + "]";
                ;
                Pair<String, String> vector = this.createVector(requirements);

                String reqBody = "{\"query\": " +
                        "{\"function_score\": {" +
                        "\"query\": { \"bool\": { \"filter\": { \"terms\":{" +
                        "\"blueprintID\": " + blueprintIdList + "}}}}," +
                        "\"functions\": [" +
                        "{\"script_score\" : {" +
                        "\"script\":{" +
                        "\"params\":{" +
                        "\"vector\": " + vector.getValue() + "," +
                        "\"field_labels\":" + vector.getKey() + "}," +
                        "\"source\":\"double distance(String s1, String s2) { if (s1.equals(s2)) { return 0; } if (s1.length() == 0) { return s2.length(); } if (s2.length() == 0) { return s1.length(); } double[] v0 = new double[s2.length() + 1]; double[] v1 = new double[s2.length() + 1]; double[] vtemp; for (int i = 0; i < v0.length; i++) { v0[i] = i; } for (int i = 0; i < s1.length(); i++) { v1[0] = i + 1; double minv1 = v1[0]; for (int j = 0; j < s2.length(); j++) { double cost = 1; if (s1.charAt(i) == s2.charAt(j)) { cost = 0; } v1[j + 1] = Math.min(v1[j] + 1,Math.min(v0[j + 1] + 1, v0[j] + cost)); minv1 = Math.min(minv1, v1[j + 1]); } vtemp = v0; v0 = v1; v1 = vtemp; } return v0[s2.length()]; } def drop(def obj,def index){ def new_obj=[]; for(int dr=0;dr<obj.length;dr++){ if(dr!=index){ new_obj.add(obj[dr]); } } return new_obj; } def getProperty(def obj,def prop){ def new_obj = obj; if((obj != null) && (prop.length > 0)){ if(prop[0] instanceof Integer){ for(int elems=0;elems<obj.length;elems++){ new_obj = getProperty(obj[elems],drop(prop,0)); if(new_obj!=null){ break; } } } else{ new_obj = getProperty(obj[prop[0]],drop(prop,0)); } } return new_obj; } def vector_B=[]; for(int i=0;i<params.field_labels.length;i++){ def path = []; String[] splits = /\\\\./.split('requirements.'+params.field_labels[i]); for(int j=0;j<splits.length;j++){ def cur = splits[j]; try{ cur = Integer.parseInt(splits[j]); }catch(Exception ex){} path.add(cur); } def cur2=getProperty(params._source,path); if(cur2 instanceof String){ cur2=distance(params.vector[i],cur2); } vector_B.add(cur2); } double dotProduct=0.0; double normA=0.0; double normB=0.0; for(int i=0;i<params.vector.length-1;i++){ def cur=params.vector[i]; if(cur instanceof String){ cur=distance(params.vector[i],params.vector[i]); } if(vector_B[i] != null){ dotProduct+=cur*vector_B[i]; normA+=cur*cur; normB+=vector_B[i]*vector_B[i]; } } if((Math.sqrt(normA)*Math.sqrt(normB)) > 0){ return (dotProduct/(Math.sqrt(normA)*Math.sqrt(normB)))*params._source['score']; } else{ return 0.0; }\"}" +
                        "}}],\"boost_mode\": \"replace\"}},\"sort\" : [\"_score\"]}";

                HttpClient httpClient;
                if (EsAuth.equals("basic")) {
                    CredentialsProvider provider = new BasicCredentialsProvider();
                    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
                    provider.setCredentials(AuthScope.ANY, credentials);
                    httpClient = HttpClientBuilder.create()
                            .setDefaultCredentialsProvider(provider)
                            .build();
                } else {
                    httpClient = HttpClientBuilder.create()
                            .build();
                }

                try {
                    HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search");
                    request.addHeader("content-type", "application/json");
                    StringEntity params = new StringEntity(reqBody);
                    request.setEntity(params);
                    HttpResponse response = httpClient.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    System.out.println(reqBody);
                    if (statusCode == 200) {
                        String bodys = new BasicResponseHandler().handleResponse(response);
                        JSONArray hits = (new JSONObject(bodys)).getJSONObject("hits").getJSONArray("hits");
                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject hit = hits.getJSONObject(i);
                            JSONObject source = hit.getJSONObject("_source");
                            double score = hit.getDouble("_score");
                            String bId = source.getString("blueprintID");
                            ratings.merge(bId, new Pair<Float, Integer>((float) score, 1), (v1, v2) -> new Pair<Float, Integer>(v1.getKey() + v2.getKey(), v1.getValue() + v2.getValue()));
                        }
                    } else {
                        System.out.println(new BasicResponseHandler().handleResponse(response));
                    }

                    for (int i = 0; i < blueprintArray.length(); i++) {
                        JSONObject obj = blueprintArray.getJSONObject(i);
                        String id = obj.getJSONObject("blueprint").getString("_id");
                        if (ratings.containsKey(id)) {
                            obj.put("userRating", ratings.get(id).getKey() / (float) ratings.get(id).getValue());
                            blueprintArray.put(i, obj);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blueprintArray.toString();
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
