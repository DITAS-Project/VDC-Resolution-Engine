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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


public class PurchaseHandlerService {

    @Value("${elasticsearch.index}")
    private String EsIndex;

    @Value("${purchase.index}")
    private String PurchIndex;

    @Value("${eshost}")
    private String EsHost;

    @Value("${elasticsearch.auth}")
    private String EsAuth;

    @Value("${elasticsearch.user}")
    private String EsUser;

    @Value("${elasticsearch.pass}")
    private String EsPass;

    @Value("${elasticsearch.port}")
    private int EsPort;

    Purchase purchase;

    public HttpResponse<String> push() throws UnirestException, NullPointerException {
        return push(this.purchase);
    }

    public HttpResponse<String> push(Purchase purc) throws UnirestException, NullPointerException {
        String reqBody = purc.toJson().toString();
        if(EsAuth.equals("basic")) {
            return Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .body(reqBody)
                    .asString();
        }else {
            return Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .body(reqBody)
                    .asString();
        }
    }

    public ArrayList<HttpResponse<String>> pushAll(String FilePath) throws UnirestException, NullPointerException, IOException {
        ArrayList<HttpResponse<String>> results = new ArrayList<HttpResponse<String>>();
        File file = new File(FilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null){
            try{
                JSONObject curLine = new JSONObject(st);
                Purchase curPurc = new Purchase(curLine.getString("userID"),curLine.getString("blueprintID"),(float)curLine.getDouble("score"),curLine.getJSONObject("requirements"));
                results.add(push(curPurc));
            }catch(Exception ex){
                continue;
            }
        }
        return results;
    }

    public ArrayList<HttpResponse<String>> pushRandom(int num) throws UnirestException, NullPointerException, IOException {
        ArrayList<HttpResponse<String>> results = new ArrayList<HttpResponse<String>>();
        String[] blueprintIDs={"5be598ca16af09d17d6085ef","5be598b916af09d17d6085e6","5be598df16af09d17d6085f8","5be5989016af09d17d6085d4","5be5993b16af09d17d608608","5be598a416af09d17d6085dd"};
        String[] userIDs={"5be598ca17af09d17d6085ef","5be598b917af09d17d6085e6","5be598df17af09d17d6085f8","5be5989017af09d17d6085d4","5be5993b17af09d17d608608","5be598a417af09d17d6085dd"};
        Random rand = new Random(4);
        for(int i=0;i<num;i++){
            JSONObject requirments = new JSONObject("{\"dataUtility\": [" +
                    "{" +
                    "\"id\": \"volume_8000\"," +
                    "\"description\": \"volume 8000\"," +
                    "\"type\": \"Volume\"," +
                    "\"properties\": {" +
                    "\"volume\": {" +
                    "\"minimum\": "+(5000+rand.nextInt(5001))+"," +
                    "\"unit\": \"tuple\"" +
                    "}" +
                    "}" +
                    "}," +
                    "{" +
                    "\"id\": \"accuracy_08\"," +
                    "\"description\": \"Accuracy\"," +
                    "\"type\": \"Accuracy\"," +
                    "\"properties\": {" +
                    "\"accuracy\": {" +
                    "\"minimum\": "+rand.nextFloat()+"," +
                    "\"unit\": \"none\"" +
                    "}" +
                    "}" +
                    "}," +
                    "{" +
                    "\"id\": \"processCompleteness_8\"," +
                    "\"description\": \"Process completeness 8\"," +
                    "\"type\": \"Process completeness\"," +
                    "\"properties\": {" +
                    "\"completeness\": {" +
                    "\"minimum\": "+rand.nextInt(16)+"," +
                    "\"unit\": \"percentage\"" +
                    "}" +
                    "}" +
                    "}," +
                    "{" +
                    "\"id\": \"scaleUpMemory\"," +
                    "\"description\": \"scale-up memory\"," +
                    "\"type\": \"Scale-up\"," +
                    "\"properties\": {" +
                    "\"ramGain\": {" +
                    "\"unit\": \"percentage\"," +
                    "\"value\": "+rand.nextInt(301)+"" +
                    "}," +
                    "\"ramLimit\": {" +
                    "\"unit\": \"percentage\"," +
                    "\"value\": "+rand.nextInt(101)+"" +
                    "}" +
                    "}" +
                    "}," +
                    "{" +
                    "\"id\": \"scaleUpSpace\"," +
                    "\"description\": \"scale-up space\"," +
                    "\"type\": \"Scale-up\"," +
                    "\"properties\": {" +
                    "\"spaceGain\": {" +
                    "\"unit\": \"percentage\"," +
                    "\"value\": "+rand.nextInt(151)+"" +
                    "}," +
                    "\"spaceLimit\": {" +
                    "\"unit\": \"percentage\"," +
                    "\"value\": "+rand.nextInt(101)+"" +
                    "}" +
                    "}" +
                    "}" +
                    "]}");
            results.add(push(new Purchase(userIDs[rand.nextInt(userIDs.length-1)],blueprintIDs[rand.nextInt(blueprintIDs.length-1)],rand.nextFloat(),requirments)));
        }
        return results;
    }

    public String clearPurchases() throws UnirestException {
        String res = "";

        JSONArray hits = getAllRecords();
        String reqBody = "";
        for (int i = 0; i < hits.length(); i++) {
            reqBody+="{ \"delete\" : { \"_index\" : \"ditas\", \"_type\" : \"purchaseinfo\", \"_id\" : \""+hits.getJSONObject(i).getString("_id")+"\" } }\n";
        }
        HttpResponse<String> data;
        if(EsAuth.equals("basic")) {
            data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .body(reqBody)
                    .asString();
        }else{
            data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .body(reqBody)
                    .asString();
        }
        res = data.getCode() + ":" + data.getBody();
        return res;
    }

    public JSONArray getAllRecords() throws UnirestException {
        HttpResponse<String> data;
        if(EsAuth.equals("basic")) {
            data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .asString();
        }else{
            data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("Method", "POST")
                    .asString();
        }
        try{
            JSONObject respJSON= new JSONObject(data.getBody());
            JSONArray hits = respJSON.getJSONObject("hits").getJSONArray("hits");
            int total = respJSON.getJSONObject("hits").getInt("total");
            if(total>10){
                if(EsAuth.equals("basic")) {
                    data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search?size=" + total + 2)
                            .header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                            .header("Content-Type", "application/json")
                            .header("cache-control", "no-cache")
                            .header("Method", "POST")
                            .asString();
                }else{
                    data = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search?size=" + total + 2)
                            .header("Content-Type", "application/json")
                            .header("cache-control", "no-cache")
                            .header("Method", "POST")
                            .asString();
                }
                respJSON= new JSONObject(data.getBody());
                hits = respJSON.getJSONObject("hits").getJSONArray("hits");
            }
            return hits;
        }catch (Exception ex){
            return null;
        }
    }

    public PurchaseHandlerService(String UserID, String BlueprintID, float UserScore, JSONObject Requirements) {
        this.purchase = new Purchase(UserID,BlueprintID,UserScore,Requirements);
    }

    public PurchaseHandlerService(Purchase purchase) {
        this.purchase = purchase;
    }

    private class Purchase{
        String UserID;
        String BlueprintID;
        float UserScore;
        JSONObject Requirements;

        public Purchase(String userID, String blueprintID, float userScore, JSONObject requirements) {
            UserID = userID;
            BlueprintID = blueprintID;
            UserScore = userScore;
            Requirements = requirements;
        }

        public JSONObject toJson(){
            JSONObject toJson = new JSONObject();
            toJson.put("userID",this.UserID);
            toJson.put("blueprintID",this.BlueprintID);
            toJson.put("score",this.UserScore);
            toJson.put("requirements",this.Requirements);
            return toJson;
        }
    }
}


