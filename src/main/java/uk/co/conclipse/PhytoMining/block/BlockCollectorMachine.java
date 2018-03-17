package uk.co.conclipse.PhytoMining.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.conclipse.PhytoMining.PhytoMining;

public class BlockCollectorMachine extends Block implements ITileEntityProvider {

    // Used for visuals only
    public static final IProperty<Boolean> IS_HANDLES = PropertyBool.create("is_handles");

    public BlockCollectorMachine() {
        super(Material.ROCK);
        setUnlocalizedName(PhytoMining.MODID + ".collectorMachine");
        setRegistryName("collectorMachine");
        setDefaultState(blockState.getBaseState().withProperty(IS_HANDLES, false));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(IS_HANDLES, false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_HANDLES);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        // Bind our TESR to our tile entity
        ClientRegistry.bindTileEntitySpecialRenderer(TileCollectorMachine.class, new RendererCollectorMachine());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCollectorMachine();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    private TileCollectorMachine getTE(World world, BlockPos pos) {
        return (TileCollectorMachine) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileCollectorMachine te = getTE(world, pos);
            if (te.getStack().isEmpty()) {
                if (!player.getHeldItem(hand).isEmpty()) {
                    // There is no item in the pedestal and the player is holding an item. We move that item
                    // to the pedestal
                    te.setStack(player.getHeldItem(hand));
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                    // Make sure the client knows about the changes in the player inventory
                    player.openContainer.detectAndSendChanges();
                }
            } else {
                // There is a stack in the pedestal. In this case we remove it and try to put it in the
                // players inventory if there is room
                ItemStack stack = te.getStack();
                te.setStack(ItemStack.EMPTY);
                if (!player.inventory.addItemStackToInventory(stack)) {
                    // Not possible. Throw item in the world
                    EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY()+1, pos.getZ(), stack);
                    world.spawnEntity(entityItem);
                } else {
                    player.openContainer.detectAndSendChanges();
                }
            }
        }

        // Return true also on the client to make sure that MC knows we handled this and will not try to place
        // a block on the client
        return true;
    }
}