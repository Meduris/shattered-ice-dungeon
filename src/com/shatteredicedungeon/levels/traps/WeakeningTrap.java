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
package com.shatteredicedungeon.levels.traps;

import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.actors.Actor;
import com.shatteredicedungeon.actors.Char;
import com.shatteredicedungeon.actors.buffs.Buff;
import com.shatteredicedungeon.actors.buffs.Slow;
import com.shatteredicedungeon.actors.buffs.Weakness;
import com.shatteredicedungeon.effects.CellEmitter;
import com.shatteredicedungeon.effects.particles.ShadowParticle;
import com.shatteredicedungeon.sprites.TrapSprite;

public class WeakeningTrap extends Trap{

	{
		name = "Weakening trap";
		color = TrapSprite.GREEN;
		shape = TrapSprite.WAVES;
	}

	@Override
	public void activate() {
		if (Dungeon.visible[ pos ]){
			CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
		}

		Char ch = Actor.findChar( pos );
		if (ch == Dungeon.hero){
			Buff.prolong( ch, Weakness.class, Weakness.duration(ch)*2f);
		} else if (ch != null) {
			Buff.prolong( ch, Slow.class, Slow.duration(ch));
		}
	}

	@Override
	public String desc() {
		return "Dark magic in this trap sucks the energy out of anything that comes into contact with it.";
	}
}
