package chai;

import chesspresso.Chess;
import chesspresso.position.Position;
import chesspresso.move.IllegalMoveException;

//MinMaxAI class uses min-max algorithm to find best move for CPU
public class MinMaxAI implements ChessAI {
	
	int iteratedDepthLimit;
	int nodesExplored;
	
	//constructor that sets a final depth limit for the search problem; more
	//depth means better decision but takes longer time
	public MinMaxAI(int d) {
		iteratedDepthLimit = d;
	}
	
	//getMove method that uses iterative deepening and our depth-limited get move
	//function; if time ever runs out, best move found so far is returned
	public short getMove(Position position) {
		nodesExplored = 0;
//		short bestMove = 0;
//		boolean timeOut = false;
//		
//		for (int i = 1; i <= iteratedDepthLimit; i++) {
//			bestMove = getDepthLimitedMove(position, i);
//			
//			if (timeOut) {
//				return bestMove;
//			}
//		}
//		
//		return bestMove;
		return getDepthLimitedMove(position, iteratedDepthLimit);
	}
	
	//depth-limited get move function; takes in a depth and uses Min-Max algorithm
	//to return best move
	public short getDepthLimitedMove(Position position, int depthLimit) {
		PositionDataNode positionNode = new PositionDataNode(position);
		
		short[] moves = positionNode.getPosition().getAllMoves();
		
		int startMaxValue = -2147483647;
		int tempMinValue;
		short bestMove = 0;
		
		for (int i = 0; i < moves.length; i++) {
			nodesExplored = nodesExplored + 1;
			try {
				positionNode.getPosition().doMove(moves[i]);
				positionNode.incrementDepth();
				}
				
				catch (IllegalMoveException exception) {
					System.out.println("IllegalMoveException");
				}
			
			tempMinValue = getMinValue(positionNode, depthLimit);
			
			if (startMaxValue < tempMinValue) {
				startMaxValue = tempMinValue;
				bestMove = moves[i];
			}
			
			positionNode.getPosition().undoMove();
			positionNode.decrementDepth();
		}
		System.out.println(nodesExplored);
		return bestMove;
	}
	
	//getMaxValue method; recursively calls getMinValue method; one of two
	//components for the min-max algorithm
	public int getMaxValue(PositionDataNode positionNode, int depthLimit) {
		if (cutoffTest(positionNode, depthLimit)) {
			return utilityCalculator(positionNode.getPosition());
		}
		
		int max = -2147483647;
		
		short[] moves = positionNode.getPosition().getAllMoves();
		
		for (int i = 0; i < moves.length; i++) {
			nodesExplored = nodesExplored + 1;
			try {
				positionNode.getPosition().doMove(moves[i]);
				positionNode.incrementDepth();
				}
				
				catch (IllegalMoveException exception) {
					System.out.println("IllegalMoveException");
				}
			
			max = Math.max(max, getMinValue(positionNode, depthLimit));
			
			positionNode.getPosition().undoMove();
			positionNode.decrementDepth();
		}
		
		return max;
	}
	
	//getMinValue method; recursively calls getMaxValue method; one of two
	//components for the min-max algorithm
	public int getMinValue(PositionDataNode positionNode, int depthLimit) {
		if (cutoffTest(positionNode, depthLimit)) {
			return utilityCalculator(positionNode.getPosition());
		}
		
		int min = 2147483647;
		
		short[] moves = positionNode.getPosition().getAllMoves();
		
		for (int i = 0; i < moves.length; i++) {
			nodesExplored = nodesExplored + 1;
			try {
			positionNode.getPosition().doMove(moves[i]);
			positionNode.incrementDepth();
			}
			
			catch (IllegalMoveException exception) {
				System.out.println("IllegalMoveException");
			}
			
			min = Math.min(min, getMaxValue(positionNode, depthLimit));
			positionNode.getPosition().undoMove();
			positionNode.decrementDepth();
		}
		
		return min;
	}
	
	//cut-off test to check if search/evaluation should be stopped at node
	//returns true if either (1) maximum depth is reached or (2) game is over
	public boolean cutoffTest(PositionDataNode positionNode, int depthLimit) {
		return (positionNode.getDepth() >= depthLimit || positionNode.getPosition().isTerminal());
	}
	
	//returns utility of state; if terminal state, returns high MAX, MIN or 0; if
	//not terminal state, return an evaluated value
	public int utilityCalculator(Position position) {
		
		if (position.getToPlay() == Chess.WHITE) {
			if (position.isMate()) {
				return -2147483647;
			}
			
			if (position.isStaleMate()) {
				return 0;
			}
			
			else {
				return evaluatePosition(position);
			}
		}
		
		if (position.getToPlay() == Chess.BLACK) {
			if (position.isMate()) {
				return 2147483647;
			}
			
			if (position.isStaleMate()) {
				return 0;
			}
			
			else {
				return -evaluatePosition(position);
			}
		}
		
		return 0;
		
	}
	
	//evaluates a utility for non-terminal nodes based on material value;
	//also takes into consideration check positions
	public int evaluatePosition(Position position) {
		int materialValue = position.getMaterial();
		
//		if (position.isCheck()) {
//			materialValue = materialValue - 500;
//		}
		
		return materialValue;
	}
}
