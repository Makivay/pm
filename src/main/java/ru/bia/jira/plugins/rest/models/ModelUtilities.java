package ru.bia.jira.plugins.rest.models;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import ru.bia.jira.plugins.ao.NoticeEntity;

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
            while (matcher.find()) {
                matchResult = matcher.toMatchResult();
//                answer.append(matchResult.toString());
            }
            if (matchResult != null) {
//                matchResult = matcher.toMatchResult();
                answer.append(matchResult.group());
//                answer.deleteCharAt(0);
            } else {
                answer.append("can't find version'");
            }
        } catch (Exception e) {
            answer.append(e.toString());
        }
        return answer.toString();
    }

    public static String getCondition(JSONArray array, String componenet) {
        JSONObject answer = new JSONObject();
        try {
            JSONObject object;
            boolean found = false;
            for (int i = 0; i < array.length(); i++) {
                object = array.getJSONObject(i);
                if (object.get("key").equals(componenet)) {
                    found = true;
                    if (object.get("enabled").equals(true)) {
                        answer.put("enabled", "enabled");
                    } else {
                        answer.put("enabled", "disabled");
                    }

                    answer.put("version", object.get("version"));
                }
            }
            if (!found) {
                answer.put("enabled", "NOT INSTALLED");
                answer.put("version", "null");
            }
        } catch (Exception e) {
        }
        return answer.toString();
    }

    public static boolean getNotice(NoticeEntity[] noticeEntities, String component) {
        for (NoticeEntity noticeEntity : noticeEntities) {
            if (noticeEntity.getComponent().equals(component)) {
                return true;
            }
        }
        return false;
    }
}
