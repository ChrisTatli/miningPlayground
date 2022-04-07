package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends CompliantMiner implements Miner {
    protected Block currentHead;
    private double alpha;
    private double rewardTotal;
    private int totalRewards;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
	public Block currentlyMiningAt(){
		return currentHead;
	}

	@Override
	public Block currentHead(){
		return currentHead;
	}

	@Override 
	public void blockMined(Block block, boolean isMinerMe){
        if(isMinerMe){
            if(block.getHeight() > currentHead.getHeight()){
                this.currentHead = block;
                //this.rewardTotal += block.getBlockValue();
            }
        } else {
            if(block.getHeight() > currentHead.getHeight()){
                double averageReward = rewardTotal/totalRewards;             
                if(alpha*alpha*(averageReward+block.getBlockValue())> alpha*averageReward){
                    this.currentHead = block.getPreviousBlock();
                    
                } else {
                    this.currentHead = block;
                    //this.rewardTotal += block.getBlockValue();
                }
            }
        }
        rewardTotal += block.getBlockValue();
        totalRewards ++;
	}
    

	@Override 
	public void initialize(Block genesis, NetworkStatistics networkStatistics){
		this.currentHead = genesis;
        this.rewardTotal = 0;
        this.totalRewards = 0;
	}

	@Override
	public void networkUpdate(NetworkStatistics statistics){
		this.alpha = this.getHashRate() / (double) statistics.getTotalHashRate();             
	}
	
}

