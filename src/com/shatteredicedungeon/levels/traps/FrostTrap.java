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

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.ResultDescriptions;
import com.shatteredicedungeon.actors.Actor;
import com.shatteredicedungeon.actors.Char;
import com.shatteredicedungeon.actors.buffs.Chill;
import com.shatteredicedungeon.actors.buffs.Frost;
import com.shatteredicedungeon.effects.Splash;
import com.shatteredicedungeon.items.Heap;
import com.shatteredicedungeon.sprites.TrapSprite;
import com.shatteredicedungeon.utils.GLog;
import com.shatteredicedungeon.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class FrostTrap extends Trap {

	{
		name = "Frost trap";
		color = TrapSprite.WHITE;
		shape = TrapSprite.STARS;
	}

	@Override
	public void activate() {

		if (Dungeon.visible[ pos ]){
			Splash.at( sprite.center(), 0xFFB2D6FF, 10);
			Sample.INSTANCE.play( Assets.SND_SHATTER );
		}

		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null) heap.freeze();

		Char ch = Actor.findChar(pos);
		if (ch != null){
			ch.damage(Random.NormalIntRange(1 , Dungeon.depth), this);
			Chill.prolong(ch, Frost.class, 10f + Random.Int(Dungeon.depth));
			if (!ch.isAlive() && ch == Dungeon.hero){
				Dungeon.fail( Utils.format(ResultDescriptions.TRAP, name) );
				GLog.n("You succumb to the freezing trap...");
			}
		}
	}

	@Override
	public String desc() {
		return "When activated, chemicals in this trap will trigger a powerful snap-frost at its location.";
	}
}
