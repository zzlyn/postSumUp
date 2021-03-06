package process;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Parse comments recursively, later change to iteratively
 */
public class ThreadRecurseProcessor {

	private ArrayList<String> comments = new ArrayList<>();

	private String jsonString;

	public ThreadRecurseProcessor(String jContent) {
		jsonString = jContent;
	}

	/**
	 * for every child of the top layer JSON array (layer 1 comments), retrieve
	 * all of its comments
	 * 
	 * @param response
	 */
	public void parsePost() throws ParseException {

		// Process original post
		JSONArray jPost = (JSONArray) new JSONParser().parse(jsonString);

		JSONObject dataJsonObj = (JSONObject) new JSONParser().parse(jPost.get(1).toString());

		JSONArray childrenData = (JSONArray) new JSONParser()
				.parse(((JSONObject) dataJsonObj.get("data")).get("children").toString());

		// Process comments & replies
		for (int k = 0; k < childrenData.size(); k++) {
			JSONObject topLayerComment = (JSONObject) ((JSONObject) childrenData.get(k)).get("data");
			parseReplies(topLayerComment);
		}
	}

	/**
	 * Recursively retrieve all comments embedded/children in this comment
	 */
	private void parseReplies(JSONObject comment) {

		if (comment.get("body") == null) {
			return;
		}

		comments.add((String) comment.get("body"));
		JSONObject replies = null;

		try {
			replies = (JSONObject) comment.get("replies");
		} catch (ClassCastException e) {
			return;
		}

		// this comment has replies
		JSONArray children = (JSONArray) (((JSONObject) replies.get("data")).get("children"));

		for (int count = 0; count < children.size(); count++) {
			JSONObject dataOut = (JSONObject) children.get(count);
			JSONObject dataItself = (JSONObject) dataOut.get("data");
			parseReplies(dataItself);
		}

	}

	/**
	 * return array of comments
	 */
	public ArrayList<String> getComments() {
		return comments;
	}

}