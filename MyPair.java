/**
 * Class implement a pair object.
 * Note javafx is not available on the core teaching servers hence we are
 * developing our own.
 *
 * @author Jeffrey Chan, 2019
 */
class MyPair {
	private String mVert;
	private Integer mWeight;

	public MyPair(String vert, Integer weight) {
		mVert = vert;
		mWeight = weight;
	}


	public String getKey() {
		return mVert;
	}

	public Integer getValue() {
		return mWeight;
	}

  public void setValue(Integer value){
    this.mWeight = value;
  }

} // end of class MyPair
