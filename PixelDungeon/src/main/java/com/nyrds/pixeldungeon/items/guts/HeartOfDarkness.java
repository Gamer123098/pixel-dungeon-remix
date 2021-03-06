package com.nyrds.pixeldungeon.items.guts;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.items.rings.Artifact;
import com.watabou.pixeldungeon.items.rings.ArtifactBuff;
import com.watabou.pixeldungeon.ui.BuffIndicator;

public class HeartOfDarkness extends Artifact {

	public HeartOfDarkness() {
		imageFile = "items/artifacts.png";
		image = 18;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	protected Buff buff() {
		return new HeartOfDarknessBuff();
	}

	public class HeartOfDarknessBuff extends ArtifactBuff {
		@Override
		public int icon() {
			return BuffIndicator.DARKVEIL;
		}

		@Override
		public String toString() {
			return Game.getVar(R.string.DarkVeil_Buff);
		}
	}
}
