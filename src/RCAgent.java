import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template.TemplateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class RCAgent extends Agent {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3655768683976018899L;


	
	StateView currentState;


	public Map<Integer, Action> initialStep(StateView newstate, HistoryView stateHistory) {
			return middleStep(newstate, stateHistory);
	}



	@Override
	public Map<Integer, Action> middleStep(StateView currentState, HistoryView endgame) {
		Map<Integer, Action> builder = new HashMap<Integer, Action>();
		
		List<Integer> goldMines = currentState.getResourceNodeIds(Type.GOLD_MINE);
		List<Integer> trees = currentState.getResourceNodeIds(Type.TREE);
		List<Integer> UnitIDs = currentState.getUnitIds(playernum);
		
		List<Integer> townhallIDs = new ArrayList<Integer>();
		List<Integer> peasantIDs = new ArrayList<Integer>();
		List<Integer> FarmIds = new ArrayList<Integer>();

		List<Integer> barrackIDs = new ArrayList<Integer>();
		List<Integer> footmenIDs = new ArrayList<Integer>();
		
		for (int i = 0; i < UnitIDs.size(); i++) {
			int ID = UnitIDs.get(i);
			UnitView unit = currentState.getUnit(ID);
			String unitTypeName = unit.getTemplateView().getName();
			if (unitTypeName.equals("Footman"))
				footmenIDs.add(ID);
			if (unitTypeName.equals("Farm"))
				FarmIds.add(ID);
			if (unitTypeName.equals("Peasant"))
				peasantIDs.add(ID);
			if (unitTypeName.equals("Barracks"))
				barrackIDs.add(ID);
			if (unitTypeName.equals("TownHall"))
				townhallIDs.add(ID);
		
		}

	
		
		
		
		if((currentState.getResourceAmount(playernum, ResourceType.GOLD) < 4000)  && ((currentState.getResourceAmount(playernum, ResourceType.GOLD) < 4000))){ 
			
				for (Integer peasantId : peasantIDs) {

				Action b =  new TargetedAction(peasantId, ActionType.PRIMITIVEMOVE, 5);
				if (currentState.getUnit(peasantId).getCargoAmount() > 0) {
					
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallIDs.get(0));
				}
				else {
					
					if (currentState.getResourceAmount(playernum, ResourceType.GOLD) < currentState
							.getResourceAmount(playernum, ResourceType.WOOD)) {
						b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, goldMines.get(0));
					} else {
						b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, trees.get(0));
					}
						}
				builder.put(peasantId, b);
			}
				}
	
			if(peasantIDs.size() < 3) 
				if(currentState.getResourceAmount(playernum, ResourceType.GOLD) >= 400)
				{
					TemplateView peasantTemplate = currentState.getTemplate(playernum, "Peasant");
					int peasantTemplateID = peasantTemplate.getID();
					int townhallID = townhallIDs.get(0);
					builder.put(townhallID, Action.createCompoundProduction(townhallID, peasantTemplateID));
				}
			
			
			
			if ((barrackIDs.size() != 1 && peasantIDs.size() == 3) ) {
				if(currentState.getResourceAmount(playernum, ResourceType.GOLD) >= 700) {
					if(currentState.getResourceAmount(playernum, ResourceType.WOOD) >= 400) {
						TemplateView barrackTemplate = currentState.getTemplate(playernum, "Barracks");
						int barrackID = barrackTemplate.getID();
						int  peasantID = peasantIDs.get(0);
						builder.put(peasantID, Action.createPrimitiveBuild(peasantID,barrackID));
			}
				}
					}
			
			
		if ((FarmIds.size() != 1 && peasantIDs.size() == 3) ) { 
		
			if(currentState.getResourceAmount(playernum, ResourceType.GOLD) >= 500) {
				if(currentState.getResourceAmount(playernum, ResourceType.WOOD) >= 250) {
					TemplateView farmTemplate = currentState.getTemplate(playernum, "Farm");
					int farmID = farmTemplate.getID();
					int  peasanTemplateID = peasantIDs.get(0);
					builder.put(peasanTemplateID, Action.createPrimitiveBuild(peasanTemplateID, farmID));
			}
				}
					}
		
		
		
		
		
		if ((footmenIDs.size() != 2 && peasantIDs.size() == 3 && barrackIDs.size() == 1) ) {
			if(currentState.getResourceAmount(playernum, ResourceType.GOLD) >= 600) {
					TemplateView footmanTemplate = currentState.getTemplate(playernum, "Footman");
					int footmanID = footmanTemplate.getID();
					int  peasanTemplateID = barrackIDs.get(0);
					
					builder.put(peasanTemplateID, Action.createPrimitiveBuild(peasanTemplateID, footmanID));
			}
			
			
		}
		
		
		
		if (footmenIDs.size() ==2) {
		for(Integer townhallID : footmenIDs)
		{
			int peasanTemplateID = 0;
				for (Integer peasanTemplateIDs : currentState.getUnitIds(1)) {
					if(peasanTemplateIDs != playernum) {
						peasanTemplateID = peasanTemplateIDs;
					}
					
				}
				builder.put(townhallID, Action.createCompoundAttack(townhallID, peasanTemplateID));
		}
		
		
		}	
		

		return builder;
		
	}
	
	public static String getUsage() {
		return "Two arguments, amount of gold to gather and amount of wood to gather";
	}

		@Override
		public void savePlayerData(OutputStream os) {	}
		@Override
		public void loadPlayerData(InputStream is) { }

		@Override
		public void terminalStep(StateView newstate, History.HistoryView statehistory) {
			// TODO Auto-generated method stub
			
		}
		public RCAgent(int playernum) {
			super(playernum);
		}


}
