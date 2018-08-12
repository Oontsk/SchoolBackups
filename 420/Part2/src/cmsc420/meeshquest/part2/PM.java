package cmsc420.meeshquest.part2;

import cmsc420.geom.Geometry2D;

public abstract class PM extends Quadtree {
	
	protected abstract class PMGray extends Gray {
		private static final long serialVersionUID = 1L;
		protected PMGray(Node o) {
			super(o);
		}
		
		public boolean contains(Geometry2D a) {
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], false)) {
					return kids[i].contains(a);
				}
			}
			return false;
		}
		
		public Node add(Geometry2D a) {
			enclosedGeos.add(a);
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], false)) {
					kids[i] = kids[i].add(a);
				}
			}
			return this;
		}
	}
	
	public boolean encloses(Geometry2D a) {
		return Utilities.geosIntersect(root, a, false);
	}
	
}
