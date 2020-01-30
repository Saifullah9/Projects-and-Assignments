import java.util.*;

class Assignment implements Comparator<Assignment>{
	int number;
	int weight;
	int deadline;
	
	
	protected Assignment() {
	}
	
	protected Assignment(int number, int weight, int deadline) {
		this.number = number;
		this.weight = weight;
		this.deadline = deadline;
	}
	
	
	
	/**
	 * This method is used to sort to compare assignment objects for sorting. 
	 * Return -1 if a1 > a2
	 * Return 1 if a1 < a2
	 * Return 0 if a1 = a2 
	 */
	@Override
	public int compare(Assignment a1, Assignment a2) {
		// TODO Implement this
		int weightX = a1.weight;
		int deadlineX = a1.deadline;
		
		int weightY = a2.weight;
		int deadlineY = a2.deadline;
		
		if(deadlineX == deadlineY) {
			if(weightX == weightY) {
				return 0;
			} else if (weightX > weightY) {
				return -1;
			} else if (weightX < weightY) {
				return 1;
			}
		}
		
		if(deadlineX > deadlineY) {
			return 1;
		}
		if(deadlineX < deadlineY) {
			return -1;
		}
		
		return 0;
	}
}

public class HW_Sched {
	ArrayList<Assignment> Assignments = new ArrayList<Assignment>();
	int m;
	int lastDeadline = 0;
	
	protected HW_Sched(int[] weights, int[] deadlines, int size) {
		for (int i=0; i<size; i++) {
			Assignment homework = new Assignment(i, weights[i], deadlines[i]);
			this.Assignments.add(homework);
			if (homework.deadline > lastDeadline) {
				lastDeadline = homework.deadline;
			}
		}
		m =size;
	}
	
	
	/**
	 * 
	 * @return Array where output[i] corresponds to the assignment 
	 * that will be done at time i.
	 */
	public int[] SelectAssignments() {
		//TODO Implement this
		
		//Sort assignments
		//Order will depend on how compare function is implemented
		Collections.sort(Assignments, new Assignment());
		
		// If schedule[i] has a value -1, it indicates that the 
		// i'th timeslot in the schedule is empty
		int[] homeworkPlan = new int[lastDeadline];
		for(int x = 0; x < homeworkPlan.length; x++ ) {
			homeworkPlan[x] = -1;
		}
		for (int i=0; i < homeworkPlan.length; ++i) {
			Assignment assignment1 = Assignments.get(i);
			System.out.println(assignment1.number);
			if(i == 0) {
				homeworkPlan[i] = assignment1.number ;
			} else if( i < Assignments.size() - 1){
				
				Assignment assignment2 = Assignments.get(i + 1);
				System.out.println(assignment1.deadline + "the conditdsdddion");
				boolean b = true;
				int x = 0;
				while(b){
					 x++;
				if ((i < assignment1.deadline) && (homeworkPlan[i-1] != assignment1.number )){ 
					
					homeworkPlan[i] = assignment1.number;
					System.out.println(assignment1.number + "the condition");
					x = 0;
					 b = false;
					} else {
						
						assignment1 = Assignments.get(i + x);
						System.out.println(assignment1.number + "finally");
						
					}
				}
				
//				else {
//				
//				System.out.println(-1);
//		
//			}
	}
			
		}
		
		
		
		
	
		
		return homeworkPlan;
	}
}
	



