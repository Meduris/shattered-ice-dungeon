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
package com.shatteredicedungeon.items.rings;


public class RingOfMight extends Ring {

	{
		name = "Ring of Might";
	}

	@Override
	protected RingBuff buff( ) {
		return new Might();
	}

	@Override
	public String desc() {
		return isKnown() ?
				"This ring enhances the physical traits of the wearer, " +
				"granting them greater physical strength and constitution. " +
				"A degraded ring will weaken the wearer." :
				super.desc();
	}

	public class Might extends RingBuff {
	}
}
