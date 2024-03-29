package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class MajorityMiner extends CompliantMiner implements Miner {
    protected Block currentHead;
    private boolean isMajorityMiner;
    
    public MajorityMiner(String id, int hashRate, int connectivity) {
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
            if (block.getHeight() > currentHead.getHeight()){
                this.currentHead = block;
            }
        } else {
            if (!isMajorityMiner && block.getHeight() > currentHead.getHeight()){
                this.currentHead = block;
            }
        }
	}

	@Override 
	public void initialize(Block genesis, NetworkStatistics networkStatistics){
		this.currentHead = genesis;
	}

	@Override
	public void networkUpdate(NetworkStatistics statistics){
		if(this.getHashRate() / (double)statistics.getTotalHashRate() > 0.5){
            isMajorityMiner = true;
        } else {
            isMajorityMiner = false;
        }
	}
    
}
