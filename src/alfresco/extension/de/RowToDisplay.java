package alfresco.extension.de;

public class RowToDisplay {

	private String wfDescription;
	private String wfDueDate;
	private int numOfActiveTasks;
	private String someVarValue;
	
	public String getWfDescription() {
		return wfDescription;
	}

	public void setWfDescription(String wfDescription) {
		this.wfDescription = wfDescription;
	}

	public String getWfDueDate() {
		return wfDueDate;
	}

	public void setWfDueDate(String wfDueDate) {
		this.wfDueDate = wfDueDate;
	}

	public int getNumOfActiveTasks() {
		return numOfActiveTasks;
	}

	public void setNumOfActiveTasks(int numOfActiveTasks) {
		this.numOfActiveTasks = numOfActiveTasks;
	}

	public String getSomeVarValue() {
		return someVarValue;
	}

	public void setSomeVarValue(String someVarValue) {
		this.someVarValue = someVarValue;
	}
}
