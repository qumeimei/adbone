package adbone;

public class QueryItem {
	
	private String title;
	private String link;
	private String description;
	private double score;
	private boolean relevant;
	private int rank;
	
	public QueryItem() {
		this.relevant = false;
		this.rank = 0;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setLind(String link) {
		this.link = link;
	}
	
	public String getLink() {
		return link;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public double getScore() {
		return score;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
}
