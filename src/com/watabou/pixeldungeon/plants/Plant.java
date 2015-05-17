/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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
package com.watabou.pixeldungeon.plants;

import java.util.ArrayList;

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.ConfusionGas;
import com.watabou.pixeldungeon.actors.buffs.Barkskin;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroSubClass;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.SpellSprite;
import com.watabou.pixeldungeon.effects.particles.LeafParticle;
import com.watabou.pixeldungeon.items.Dewdrop;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.food.Food;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.PlantSprite;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Plant implements Bundlable {

	public String plantName;

	public int image;
	public int pos;

	public PlantSprite sprite;

	public void activate(Char ch) {

		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
			Buff.affect(ch, Barkskin.class).level(ch.ht() / 3);
		}
		
		effect(pos, ch);
		
		wither();
	}

	public void wither() {
		Dungeon.level.uproot(pos);

		sprite.kill();
		if (Dungeon.visible[pos]) {
			CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
		}

		if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
			if (Random.Int(5) == 0) {
				Dungeon.level.drop(Generator.random(Generator.Category.SEED),
						pos).sprite.drop();
			}
			if (Random.Int(5) == 0) {
				Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
			}
		}
	}

	private static final String POS = "pos";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		pos = bundle.getInt(POS);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(POS, pos);
	}

	public String desc() {
		return null;
	}
	
	public void effect(int pos, Char ch) {
		
	}

	public static class Seed extends Item {

		public static final String AC_PLANT = Game
				.getVar(R.string.Plant_ACPlant);
		private static final String TXT_INFO = Game.getVar(R.string.Plant_Info);
		protected static final String TXT_SEED = Game
				.getVar(R.string.Plant_Seed);

		private static final float TIME_TO_PLANT = 1f;

		{
			stackable = true;
			defaultAction = AC_THROW;
		}

		protected Class<? extends Plant> plantClass;
		protected String plantName;

		public Class<? extends Item> alchemyClass;

		@Override
		public ArrayList<String> actions(Hero hero) {
			ArrayList<String> actions = super.actions(hero);
			actions.add(AC_PLANT);
			actions.add(Food.AC_EAT);
			return actions;
		}

		@Override
		protected void onThrow(int cell) {
			if (Dungeon.level.map[cell] == Terrain.ALCHEMY || Level.pit[cell]) {
				super.onThrow(cell);
			} else {
				Dungeon.level.plant(this, cell);
			}
		}

		@Override
		public void execute(Hero hero, String action) {
			if (action.equals(AC_PLANT)) {

				hero.spend(TIME_TO_PLANT);
				hero.busy();
				((Seed) detach(hero.belongings.backpack)).onThrow(hero.pos);

				hero.getSprite().operate(hero.pos);

			} else if (action.equals(Food.AC_EAT)) {
				detach(hero.belongings.backpack);

				hero.getSprite().operate(hero.pos);
				hero.busy();

				SpellSprite.show(hero, SpellSprite.FOOD);
				Sample.INSTANCE.play(Assets.SND_EAT);

				hero.spend(Food.TIME_TO_EAT);
			}

			super.execute(hero, action);
		}
		
		@Override
		public Item burn(int cell){
			return null;
		}

		public Plant couch(int pos) {
			try {
				Sample.INSTANCE.play(Assets.SND_PLANT);
				Plant plant = plantClass.newInstance();
				plant.pos = pos;
				return plant;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}

		@Override
		public int price() {
			return 10 * quantity();
		}

		@Override
		public String info() {
			return String.format(TXT_INFO, Utils.indefinite(plantName), desc());
		}
	}
}
