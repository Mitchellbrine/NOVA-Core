package nova.core.render;

import java.util.HashSet;
import java.util.Set;

import nova.core.render.model.ModelProvider;
import nova.core.render.texture.BlockTexture;
import nova.core.render.texture.EntityTexture;
import nova.core.render.texture.ItemTexture;
import nova.core.render.texture.Texture;
import nova.core.util.transform.vector.Vector2i;

/**
 * @author Calclavia
 */
public abstract class RenderManager {

	public final Set<BlockTexture> blockTextures = new HashSet<>();
	public final Set<ItemTexture> itemTextures = new HashSet<>();
	public final Set<EntityTexture> entityTextures = new HashSet<>();
	public final Set<ModelProvider> modelProviders = new HashSet<>();

	public ItemTexture registerTexture(ItemTexture texture) {
		itemTextures.add(texture);
		return texture;
	}

	public BlockTexture registerTexture(BlockTexture texture) {
		blockTextures.add(texture);
		return texture;
	}

	public ModelProvider registerModel(ModelProvider modelProvider) {
		modelProviders.add(modelProvider);
		return modelProvider;
	}

	public EntityTexture registerTexture(EntityTexture texture) {
		entityTextures.add(texture);
		return texture;
	}

	@Deprecated
	public abstract Vector2i getDimension(Texture texture);
}
