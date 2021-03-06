/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredicedungeon.actors.mobs;

import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.actors.Char;
import com.shatteredicedungeon.actors.buffs.Terror;
import com.shatteredicedungeon.actors.hero.Hero;
import com.shatteredicedungeon.items.Gold;
import com.shatteredicedungeon.items.Honeypot;
import com.shatteredicedungeon.items.Item;
import com.shatteredicedungeon.items.artifacts.MasterThievesArmband;
import com.shatteredicedungeon.sprites.CharSprite;
import com.shatteredicedungeon.sprites.ThiefSprite;
import com.shatteredicedungeon.utils.GLog;
import com.shatteredicedungeon.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {

	protected static final String TXT_STOLE	= "%s stole %s from you!";
	protected static final String TXT_CARRIES	= "\n\n%s is carrying a _%s_. Stolen obviously.";
	
	public Item item;
	
	{
		name = "crazy thief";
		spriteClass = ThiefSprite.class;
		
		HP = HT = 20;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 10;
		
		loot = new MasterThievesArmband().identify();
		lootChance = 0.01f;

		FLEEING = new Fleeing();
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 7 );
	}

	@Override
	protected float attackDelay() {
		return 0.5f;
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (item != null) {
			Dungeon.level.drop( item, pos ).sprite.drop();
			//updates position
			if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).setHolder( this );
		}
	}

	@Override
	protected Item createLoot(){
		if (!Dungeon.limitedDrops.armband.dropped()) {
			Dungeon.limitedDrops.armband.drop();
			return super.createLoot();
		} else
			return new Gold(Random.NormalIntRange(100, 250));
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int dr() {
		return 3;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (item == null && enemy instanceof Hero && steal( (Hero)enemy )) {
			state = FLEEING;
		}

		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) {
			Dungeon.level.drop( new Gold(), pos ).sprite.drop();
		}

		return super.defenseProc(enemy, damage);
	}

	protected boolean steal( Hero hero ) {

		Item item = hero.belongings.randomUnequipped();

		if (item != null && !item.unique && item.level < 1 ) {

			GLog.w( TXT_STOLE, this.name, item.name() );
			Dungeon.quickslot.clearItem( item );
			item.updateQuickslot();

			if (item instanceof Honeypot){
				this.item = ((Honeypot)item).shatter(this, this.pos);
				item.detach( hero.belongings.backpack );
			} else {
				this.item = item.detach( hero.belongings.backpack );
				if ( item instanceof Honeypot.ShatteredPot)
					((Honeypot.ShatteredPot)item).setHolder(this);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		String desc =
				"Though these inmates roam free of their cells, this place is still their prison. " +
				"Over time, this place has taken their minds as well as their freedom. " +
				"Long ago, these crazy thieves and bandits have forgotten who they are and why they steal.\n\n" +
				"These enemies are more likely to steal and run than they are to fight. " +
				"Make sure to keep them in sight, of you might never see your stolen item again.";

		if (item != null) {
			desc += String.format( TXT_CARRIES, Utils.capitalize( this.name ), item.name() );
		}

		return desc;
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff( Terror.class ) == null) {
				if (enemySeen) {
					sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
					state = HUNTING;
				} else {
					if (item != null) GLog.n("The thief gets away with your " + item.name() + "!");
					item = null;
					state = WANDERING;
				}
			} else {
				super.nowhereToRun();
			}
		}
	}
}
