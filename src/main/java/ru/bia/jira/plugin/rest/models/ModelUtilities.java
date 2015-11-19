package ru.bia.jira.plugin.rest.models;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kmatveev on 17.11.2015.
 */
public class ModelUtilities {

    public static String getVersion(final String text) {
        StringBuilder answer = new StringBuilder();
        try {

            Matcher matcher = Pattern.compile("[^-]*[^(.jar)]").matcher(text);
            MatchResult matchResult = null;
            while(matcher.find()){
                matchResult = matcher.toMatchResult();
//                answer.append(matchResult.toString());
            }
            if(matchResult != null) {
//                matchResult = matcher.toMatchResult();
                answer.append(matchResult.group());
//                answer.deleteCharAt(0);
            }else {
                answer.append("can't find version'");
            }
        } catch (Exception e) {
            answer.append(e.toString());
        }
        return answer.toString();
    }
}
