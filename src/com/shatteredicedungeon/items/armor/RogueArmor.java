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
package com.shatteredicedungeon.items.armor;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.actors.Actor;
import com.shatteredicedungeon.actors.buffs.Blindness;
import com.shatteredicedungeon.actors.buffs.Buff;
import com.shatteredicedungeon.actors.hero.Hero;
import com.shatteredicedungeon.actors.hero.HeroClass;
import com.shatteredicedungeon.actors.mobs.Mob;
import com.shatteredicedungeon.effects.CellEmitter;
import com.shatteredicedungeon.effects.Speck;
import com.shatteredicedungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.scenes.CellSelector;
import com.shatteredicedungeon.scenes.GameScene;
import com.shatteredicedungeon.sprites.ItemSpriteSheet;
import com.shatteredicedungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class RogueArmor extends ClassArmor {
	
	private static final String TXT_FOV 		= "You can only jump to an empty location in your field of view";
	private static final String TXT_NOT_ROGUE	= "Only rogues can use this armor!";
	
	private static final String AC_SPECIAL = "SMOKE BOMB";
	
	{
		name = "rogue garb";
		image = ItemSpriteSheet.ARMOR_ROGUE;
	}
	
	@Override
	public String special() {
		return AC_SPECIAL;
	}
	
	@Override
	public void doSpecial() {
		GameScene.selectCell( teleporter );
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		if (hero.heroClass == HeroClass.ROGUE) {
			return super.doEquip( hero );
		} else {
			GLog.w( TXT_NOT_ROGUE );
			return false;
		}
	}
	
	@Override
	public String desc() {
		return
			"Wearing this dark garb, a rogue can perform a trick, that is called \"smoke bomb\" " +
			"(though no real explosives are used): he blinds enemies who could see him and jumps aside.";
	}
	
	protected static CellSelector.Listener teleporter = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {

				if (!Level.fieldOfView[target] ||
					!(Level.passable[target] || Level.avoid[target]) ||
					Actor.findChar( target ) != null) {
					
					GLog.w( TXT_FOV );
					return;
				}

				curUser.HP -= (curUser.HP / 3);
				
				for (Mob mob : Dungeon.level.mobs) {
					if (Level.fieldOfView[mob.pos]) {
						Buff.prolong( mob, Blindness.class, 2 );
						if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
						mob.sprite.emitter().burst( Speck.factory( Speck.LIGHT ), 4 );
					}
				}
				
				ScrollOfTeleportation.appear( curUser, target );
				CellEmitter.get( target ).burst( Speck.factory( Speck.WOOL ), 10 );
				Sample.INSTANCE.play( Assets.SND_PUFF );
				Dungeon.level.press( target, curUser );
				Dungeon.observe();
				
				curUser.spendAndNext( Actor.TICK );
			}
		}
		
		@Override
		public String prompt() {
			return "Choose a location to jump to";
		}
	};
}