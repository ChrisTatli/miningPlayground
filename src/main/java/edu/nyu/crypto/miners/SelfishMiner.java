package edu.nyu.crypto.miners;
import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class SelfishMiner extends CompliantMiner implements Miner {
	protected Block publicHead, privateHead;
	private int privateBranchLen, delta;
	private boolean mineSelfish;

	public SelfishMiner(String id, int hashRate, int connectivity) {
		super(id, hashRate, connectivity);
	}

	@Override
	public Block currentlyMiningAt(){
		return privateHead;
	}

	@Override
	public Block currentHead(){
		return publicHead;
	}


	public void _blockMined(Block block, boolean isMinerMe){
		if(isMinerMe){
			int diffPrev = privateHead.getHeight() - block.getHeight();
			this.privateHead = block;
			privateBranchLen++;
			if(diffPrev == 0 && privateBranchLen == 2 ){
				this.publicHead = this.privateHead;
				this.privateBranchLen = 0;
			}
		} else {
			int diffPrev = privateHead.getHeight() - block.getHeight();
			this.publicHead = block;
			if (diffPrev == 0){
				this.privateHead = block;
				this.privateBranchLen = 0; 
			} else if (diffPrev == 1){
				this.publicHead = this.privateHead;
			} else if (diffPrev == 2){
				this.publicHead = this.privateHead;
				this.privateBranchLen = 0;
			} else {
				this.publicHead = this.privateHead;
			}
		}
	}

	@Override 
	public void blockMined(Block block, boolean isMinerMe){
		if(mineSelfish){
			if(isMinerMe){
				delta = privateHead.getHeight() - publicHead.getHeight();
				if(block.getHeight() > privateHead.getHeight()){
					this.privateHead = block;
				}
			} else {
				if(block.getHeight() > publicHead.getHeight()){
					this.publicHead = block;
					delta = privateHead.getHeight() - publicHead.getHeight();
					if(delta < 0){
						this.privateHead = this.publicHead;
					} else if (delta == 0){
						this.publicHead = this.privateHead;
					} else if (delta == 1) {
						this.publicHead = this.privateHead;
					}
				}
			}
		} else { //compliant 	
			if(isMinerMe) {
				if (block.getHeight() > publicHead.getHeight()) {
					this.publicHead = block;
					this.privateHead = block;
				}
			}
			else {
				if (block.getHeight() > publicHead.getHeight()) {
					this.publicHead = block;
					this.privateHead = block;
				}
			}
		}
	}

	@Override 
	public void initialize(Block genesis, NetworkStatistics networkStatistics){
		this.publicHead = genesis;
		this.privateHead = genesis;		
	}

	@Override
	public void networkUpdate(NetworkStatistics statistics){
		double alpha = this.getHashRate() / (double) statistics.getTotalHashRate();
		this.mineSelfish = alpha > 0.25 ? true : false; // better than default if alpha is more than 0,25
	}


}
	
