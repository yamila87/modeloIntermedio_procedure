package manifest;

public class JSManifestItems {
	 private String cnt;
     private String fname;
     private String outPath;
     private String digest;
     private String query;
     
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt.trim().replace("\t", "");
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname.trim().replace("\t", "");
	}
	public String getOutPath() {
		return outPath;
	}
	public void setOutPath(String outPath) {
		this.outPath = outPath.trim().replace("\t", "");
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest.trim().replace("\t", "");
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query.trim().replace("\t", "");
	}
	
}
