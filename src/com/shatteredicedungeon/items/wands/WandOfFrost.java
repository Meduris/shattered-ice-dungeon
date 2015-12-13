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
package com.shatteredicedungeon.items.wands;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.actors.Actor;
import com.shatteredicedungeon.actors.Char;
import com.shatteredicedungeon.actors.buffs.Buff;
import com.shatteredicedungeon.actors.buffs.Chill;
import com.shatteredicedungeon.actors.buffs.FlavourBuff;
import com.shatteredicedungeon.actors.buffs.Frost;
import com.shatteredicedungeon.effects.MagicMissile;
import com.shatteredicedungeon.items.Heap;
import com.shatteredicedungeon.items.weapon.melee.MagesStaff;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.mechanics.Ballistica;
import com.shatteredicedungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfFrost extends Wand {

	{
		name = "Wand of Frost";
		image = ItemSpriteSheet.WAND_FROST;
	}

	@Override
	protected void onZap(Ballistica bolt) {

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) {
			heap.freeze();
		}

		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch != null){

			int damage = Random.NormalIntRange(5+level, 10+(level*level/3));

			if (ch.buff(Frost.class) != null){
				return; //do nothing, can't affect a frozen target
			}
			if (ch.buff(Chill.class) != null){
				damage = Math.round(damage * ch.buff(Chill.class).speedFactor());
			} else {
				ch.sprite.burst( 0xFF99CCFF, level / 2 + 2 );
			}

			processSoulMark(ch, chargesPerCast());
			ch.damage(damage, this);

			if (ch.isAlive()){
				if (Level.water[ch.pos]){
					//20+(10*level)% chance
					if (Random.Int(10) >= 8-level )
						Buff.affect(ch, Frost.class, Frost.duration(ch)*Random.Float(2f, 4f));
					else
						Buff.prolong(ch, Chill.class, 6+level);
				} else {
					Buff.prolong(ch, Chill.class, 4+level);
				}
			}
		}
	}

	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile.blueLight(curUser.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	//TODO: balancing, this could be mighty OP
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		Chill chill = defender.buff(Chill.class);
		if (chill != null && Random.Float() > chill.speedFactor()){
			//need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
			new FlavourBuff(){
				{actPriority = Integer.MIN_VALUE;}
				public boolean act() {
					Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(1f, 2f));
					return super.act();
				}
			}.attachTo(defender);
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color(0x88CCFF);
		particle.am = 0.6f;
		particle.setLifespan(1.5f);
		float angle = Random.Float(PointF.PI2);
		particle.speed.polar( angle, 2f);
		particle.acc.set( 0f, 1f);
		particle.setSize( 0f, 1.5f);
		particle.radiateXY(Random.Float(2f));
	}

	@Override
	public String desc() {
		return "This wand seems to be made out of some kind of magical ice. It grows brighter towards its " +
				"rounded tip. It feels very cold when held, but somehow your hand stays warm.\n\n" +
				"This wand shoots blasts of icy energy toward your foes, dealing significant damage and chilling, " +
				"which reduces speed. The effect seems stronger in water. Chilled and frozen enemies " +
				"take less damage from this wand, as they are already cold.";
	}
}
