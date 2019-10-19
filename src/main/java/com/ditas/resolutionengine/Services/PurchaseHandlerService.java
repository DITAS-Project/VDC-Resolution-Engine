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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.Math.min;

@Service
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

    public String push() {
        return push(this.purchase);
    }

    public String push(Purchase purc){
        String reqBody = purc.toJson().toString();

        HttpClient httpClient;
        if(EsAuth.equals("basic")) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient= HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();
        }else{
            httpClient=HttpClientBuilder.create()
                    .build();
        }

        try {
            HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + PurchIndex);
            request.addHeader("content-type", "application/json");
            StringEntity params =new StringEntity(reqBody);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = new BasicResponseHandler().handleResponse(response);
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<String> pushAll(String FilePath){
        ArrayList<String> results = new ArrayList<String>();
        File file = new File(FilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                try {
                    JSONObject curLine = new JSONObject(st);
                    Purchase curPurc = new Purchase(curLine.getString("userID"), curLine.getString("blueprintID"), (float) curLine.getDouble("score"), curLine.getJSONObject("requirements"));
                    results.add(push(curPurc));
                } catch (Exception ex) {
                    continue;
                }
            }
        }catch(Exception ex){
            System.err.println(ex.getLocalizedMessage());
        }
        return results;
    }

    public ArrayList<String> pushRandom(int num) {
        ArrayList<String> results = new ArrayList<String>();
        String[] blueprintIDs={"5be598ca16af09d17d6085ef","5be598b916af09d17d6085e6","5be598df16af09d17d6085f8","5be5989016af09d17d6085d4","5be5993b16af09d17d608608","5be598a416af09d17d6085dd"};
        String[] userIDs={"5be598ca17af09d17d6085ef","5be598b917af09d17d6085e6","5be598df17af09d17d6085f8","5be5989017af09d17d6085d4","5be5993b17af09d17d608608","5be598a417af09d17d6085dd"};
        Random rand = new Random();
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

    /**
     * Gets a remote user requirements file and randomizes it.
     * @param url The url hosting the remote requirements file.
     * @return The randomized requirements file.
     */
    public static JSONObject randomizeRemoteRequirements(String url){
        try {
            HttpClient httpClient=HttpClientBuilder.create().build();
            HttpGet urBaseRequest = new HttpGet(url);
            System.out.println(url);
            HttpResponse response = httpClient.execute(urBaseRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = new BasicResponseHandler().handleResponse(response);
            if (statusCode != 200) {
                System.err.println(responseString);
                return null;
            }
            JSONObject parsed = new JSONObject(responseString);
            if(parsed.has("attributes")){
                JSONObject atrrs = parsed.getJSONObject("attributes");
                if(atrrs.has("dataUtility")) {
                    JSONArray du = atrrs.getJSONArray("dataUtility");
                    atrrs.put("dataUtility",randomizeDataUtilityArray(du));
                }
                parsed.put("attributes",atrrs);
            }
            return parsed;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Converts the dataUtilities array to json object in order to remove index dependency on rating.
     * @return The converted JSON object.
     */
    public JSONObject arrayToObject(JSONObject obj){
        return null;
    }

    /**
     * Randomizes a pre-created dataUtilities array values.
     * @param dataUtilities The pre-created JSONArray containing the dataUtilities.
     * @return The Randomized dataUtilities JSONArray.
     */
    public static JSONArray randomizeDataUtilityArray(JSONArray dataUtilities){
        Random rand = new Random();
        if(dataUtilities != null && dataUtilities.length() > 0){
            for (int i =0;i<dataUtilities.length();i++){
                if (dataUtilities.get(i) instanceof JSONObject) {
                    JSONObject cur = dataUtilities.getJSONObject(i);
                    if(cur.has("properties")){
                        JSONObject props = cur.getJSONObject("properties");
                        if(props.has("volume")){
                            JSONObject vol = props.getJSONObject("volume");
                            int min = (5000+rand.nextInt(5001));
                            int max = (min+rand.nextInt(5001));
                            if(vol.has("minimum")){
                                vol.put("minimum",min);
                            }
                            if(vol.has("maximum")){
                                vol.put("maximum",max);
                            }
                            props.put("volume",vol);
                        }
                        if(props.has("responseTime")){
                            JSONObject rspt = props.getJSONObject("responseTime");
                            int min = (1+rand.nextInt(4));
                            int max = (min+rand.nextInt(4));
                            if(rspt.has("minimum")){
                                rspt.put("minimum",min);
                            }
                            if(rspt.has("maximum")){
                                rspt.put("maximum",max);
                            }
                            props.put("responseTime",rspt);
                        }
                        if(props.has("throughtput")){
                            JSONObject thrpt = props.getJSONObject("throughtput");
                            double min = (0.2+(((double)rand.nextInt(54))/10));
                            double max = (min+(((double)rand.nextInt(56))/10));
                            if(thrpt.has("minimum")){
                                thrpt.put("minimum",min);
                            }
                            if(thrpt.has("maximum")){
                                thrpt.put("maximum",max);
                            }
                            props.put("throughtput",thrpt);
                        }
                        if(props.has("accuracy")){
                            JSONObject acc = props.getJSONObject("accuracy");
                            int min = (50+rand.nextInt(51));
                            min = min(min,100);
                            int max = (min+rand.nextInt(51));
                            max = min(max,100);
                            if(acc.has("minimum")){
                                acc.put("minimum",min);
                            }
                            if(acc.has("maximum")){
                                acc.put("maximum",max);
                            }
                            props.put("accuracy",acc);
                        }
                        if(props.has("completeness")){
                            JSONObject comp = props.getJSONObject("completeness");
                            int min = (50+rand.nextInt(51));
                            min = min(min,100);
                            int max = (min+rand.nextInt(51));
                            max = min(max,100);
                            if(comp.has("minimum")){
                                comp.put("minimum",min);
                            }
                            if(comp.has("maximum")){
                                comp.put("maximum",max);
                            }
                            props.put("completeness",comp);
                        }
                        if(props.has("precision")){
                            JSONObject prs = props.getJSONObject("precision");
                            int min = (50+rand.nextInt(51));
                            min = min(min,100);
                            int max = (min+rand.nextInt(51));
                            max = min(max,100);
                            if(prs.has("minimum")){
                                prs.put("minimum",min);
                            }
                            if(prs.has("maximum")){
                                prs.put("maximum",max);
                            }
                            props.put("precision",prs);
                        }
                        if(props.has("timeliness")){
                            JSONObject timl = props.getJSONObject("timeliness");
                            int min = (50+rand.nextInt(51));
                            min = min(min,100);
                            int max = (min+rand.nextInt(51));
                            max = min(max,100);
                            if(timl.has("minimum")){
                                timl.put("minimum",min);
                            }
                            if(timl.has("maximum")){
                                timl.put("maximum",max);
                            }
                            props.put("timeliness",timl);
                        }
                        if(props.has("availability")){
                            JSONObject avl = props.getJSONObject("availability");
                            int min = (50+rand.nextInt(51));
                            min = min(min,100);
                            int max = (min+rand.nextInt(51));
                            max = min(max,100);
                            if(avl.has("minimum")){
                                avl.put("minimum",min);
                            }
                            if(avl.has("maximum")){
                                avl.put("maximum",max);
                            }
                            props.put("availability",avl);
                        }
                        if(props.has("ramGain")){
                            JSONObject rgain = props.getJSONObject("ramGain");
                            if(rgain.has("value")){
                                rgain.put("value",(50+rand.nextInt(151)));
                            }
                            props.put("ramGain",rgain);
                        }
                        if(props.has("ramLimit")){
                            JSONObject rlim = props.getJSONObject("ramLimit");
                            if(rlim.has("value")){
                                rlim.put("value",(50+rand.nextInt(51)));
                            }
                            props.put("ramLimit",rlim);
                        }
                        if(props.has("spaceGain")){
                            JSONObject sgain = props.getJSONObject("spaceGain");
                            if(sgain.has("value")){
                                sgain.put("value",(50+rand.nextInt(151)));
                            }
                            props.put("spaceGain",sgain);
                        }
                        if(props.has("spaceLimit")){
                            JSONObject slimit = props.getJSONObject("spaceLimit");
                            if(slimit.has("value")){
                                slimit.put("value",(50+rand.nextInt(51)));
                            }
                            props.put("spaceLimit",slimit);
                        }
                        cur.put("properties",props);
                    }
                    dataUtilities.put(i,cur);
                }
            }
            return dataUtilities;
        }
        return new JSONArray();
    }

    /**
     * Simulates a purchase by creating a randomized user requirements file and sending it for resolution.
     * @return A success or fail message.
     */
    public String makePurchase() {
        ArrayList<String> requirementsUrls= new ArrayList<String>();
        requirementsUrls.add("https://github.com/DITAS-Project/ideko-use-case/raw/master/applicationRequirements/appreq-diagnostics.json");
        requirementsUrls.add("https://github.com/DITAS-Project/ideko-use-case/raw/master/applicationRequirements/appreq-streaming.json");
        requirementsUrls.add("https://github.com/DITAS-Project/osr-use-case/raw/master/scenario13/applicationRequirements/appreq-scenario-13.json");
        Random rand = new Random();
        int pickedIndex = rand.nextInt(requirementsUrls.size());
        JSONObject randomUR = randomizeRemoteRequirements(requirementsUrls.get(pickedIndex));
        requirementsUrls.remove(pickedIndex);
        while(randomUR == null && requirementsUrls.size() > 0) {
            pickedIndex = rand.nextInt(requirementsUrls.size());
            randomUR = randomizeRemoteRequirements(requirementsUrls.get(pickedIndex));
            requirementsUrls.remove(pickedIndex);
        }
        if(randomUR == null){return null;}

        try {
            HttpClient httpClient=HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:8080/searchBlueprintByReq");
            request.addHeader("content-type", "application/json");
            StringEntity params =new StringEntity(randomUR.toString());
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = new BasicResponseHandler().handleResponse(response);
            if (statusCode != 200) {
                System.err.println(responseString);
                return responseString;
            }
            JSONArray results =new JSONArray(responseString);
            if(results.length() == 0){
                return "[]";
            }
            String[] userIDs={"5be598ca17af09d17d6085ef","5be598b917af09d17d6085e6","5be598df17af09d17d6085f8","5be5989017af09d17d6085d4","5be5993b17af09d17d608608","5be598a417af09d17d6085dd"};

            return push(new Purchase(userIDs[rand.nextInt(userIDs.length-1)],results.getJSONObject(rand.nextInt(results.length())).getJSONObject("blueprint").getString("_id"),rand.nextFloat(),randomUR));
        }catch(Exception ex){
            ex.printStackTrace();
            return ex.getStackTrace().toString();
        }
    }

    public String clearPurchases() {
        String res = "";

        JSONArray hits = getAllRecords();
        String reqBody = "";
        for (int i = 0; i < hits.length(); i++) {
            reqBody+="{ \"delete\" : { \"_index\" : \"ditas\", \"_type\" : \"purchaseinfo\", \"_id\" : \""+hits.getJSONObject(i).getString("_id")+"\" } }\n";
        }

        HttpClient httpClient;
        if(EsAuth.equals("basic")) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient= HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();
        }else{
            httpClient=HttpClientBuilder.create()
                    .build();
        }
        int statusCode = -1;
        String responseString = "";
        try {
            HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_batch");
            request.addHeader("content-type", "application/json");
            StringEntity params =new StringEntity(reqBody);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            responseString = new BasicResponseHandler().handleResponse(response);
            if (statusCode != 200) {
                System.err.println(responseString);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return statusCode+':'+responseString;
    }

    public JSONArray getAllRecords(){
        HttpClient httpClient;
        if(EsAuth.equals("basic")) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient= HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();
        }else{
            httpClient=HttpClientBuilder.create()
                    .build();
        }
        int statusCode = -1;
        String data = "";
        try {
            HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search");
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            data = new BasicResponseHandler().handleResponse(response);
            if (statusCode != 200) {
                System.err.println(data);
            }else{
                try{
                    JSONObject respJSON= new JSONObject(data);
                    JSONArray hits = respJSON.getJSONObject("hits").getJSONArray("hits");
                    int total = respJSON.getJSONObject("hits").getInt("total");
                    if(total>10){
                        request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + PurchIndex + "/_search?size=" + total + 2);
                        request.addHeader("content-type", "application/json");
                        response = httpClient.execute(request);
                        statusCode = response.getStatusLine().getStatusCode();
                        data = new BasicResponseHandler().handleResponse(response);
                        respJSON= new JSONObject(data);
                        hits = respJSON.getJSONObject("hits").getJSONArray("hits");
                    }
                    return hits;
                }catch (Exception ex){
                    return null;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public PurchaseHandlerService(String UserID, String BlueprintID, float UserScore, JSONObject Requirements) {
        this.purchase = new Purchase(UserID,BlueprintID,UserScore,Requirements);
    }

    public PurchaseHandlerService(Purchase purchase) {
        this.purchase = purchase;
    }

    public PurchaseHandlerService() {
        this.purchase = null;
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
            Requirements = new JSONObject();
            if(requirements.has("dataUtility")){
                if(requirements.get("dataUtility") instanceof  JSONArray){
                    Requirements.put("dataUtility",requirements.getJSONArray("dataUtility"));
                }
            }else if(requirements.has("attributes")){
                if(requirements.getJSONObject("attributes").has("dataUtility")) {
                    if (requirements.getJSONObject("attributes").get("dataUtility") instanceof JSONArray) {
                        Requirements.put("dataUtility", requirements.getJSONObject("attributes").getJSONArray("dataUtility"));
                    }
                }
            }

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


