import mpi.* ;

class Ring {

	static public void main(String[] args) throws MPIException {
		MPI.Init(args) ;
		int source; 
		int dest;
		int tag=50;
		int next;
		int prev;
		int message[] = new int[1];
		
		int myrank = MPI.COMM_WORLD.getRank() ;
		int size = MPI.COMM_WORLD.getSize() ;

		next = (myrank + 1) % size;
		prev = (myrank + size - 1) % size;

		if (0 == myrank) {
			message[0] = 10;

			System.out.println("Process 0 sending " + message[0] + " to rank " + next + " (" + size + " processes in ring)");
			MPI.COMM_WORLD.send(message, 1, MPI.INT, next, tag);

		}


		while (true) {

			MPI.COMM_WORLD.recv(message, 1, MPI.INT, prev, tag);
			
			if (0 == myrank) {
				--message[0];
				System.out.println("Process 0 decremented value: " + message[0]);
			}

			MPI.COMM_WORLD.send(message, 1, MPI.INT, next, tag);
			if (0 == message[0]) {
				System.out.println("Process " + myrank + " exiting");
				break;
			}
		}

		if (0 == myrank) {
			MPI.COMM_WORLD.recv(message, 1, MPI.INT, prev, tag);
		}
		
		MPI.Finalize();
	}
	
}

