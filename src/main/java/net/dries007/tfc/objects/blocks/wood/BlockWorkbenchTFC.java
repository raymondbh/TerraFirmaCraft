/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockWorkbenchTFC extends BlockWorkbench
{
    private static final EnumMap<Wood, BlockWorkbenchTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockWorkbenchTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockWorkbenchTFC(Wood wood)
    {
        super();
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setSoundType(SoundType.WOOD);
        setHardness(2.0F).setResistance(5.0F);
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "workbench");
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            playerIn.displayGui(new InterfaceCraftingTable(this, worldIn, pos));
            playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION);
            return true;
        }
    }

    public static class InterfaceCraftingTable implements IInteractionObject
    {
        //todo: replace with proper workbench mechanics
        private final BlockWorkbenchTFC workbenchTFC;
        private final World world;
        private final BlockPos position;

        public InterfaceCraftingTable(BlockWorkbenchTFC workbenchTFC, World worldIn, BlockPos pos)
        {
            this.workbenchTFC = workbenchTFC;
            this.world = worldIn;
            this.position = pos;
        }

        /**
         * Get the name of this object. For players this returns their username
         */
        public String getName()
        {
            return "crafting_table";
        }

        /**
         * Returns true if this thing is named
         */
        public boolean hasCustomName()
        {
            return false;
        }

        /**
         * Get the formatted ChatComponent that will be used for the sender's username in chat
         */
        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation(workbenchTFC.getUnlocalizedName() + ".name");
        }

        public Container createContainer(InventoryPlayer inv, EntityPlayer player)
        {
            return new ContainerWorkbenchTFC(inv, world, position, workbenchTFC);
        }

        public String getGuiID()
        {
            return "minecraft:crafting_table";
        }
    }

    public static class ContainerWorkbenchTFC extends ContainerWorkbench
    {
        //todo: replace with proper workbench mechanics
        private final World world;
        private final BlockPos pos;
        private final BlockWorkbenchTFC block;

        public ContainerWorkbenchTFC(InventoryPlayer inv, World world, BlockPos pos, BlockWorkbenchTFC block)
        {
            super(inv, world, pos);
            this.world = world;
            this.pos = pos;
            this.block = block;
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn)
        {
            if (world.getBlockState(pos).getBlock() != block)
            {
                return false;
            }
            else
            {
                return playerIn.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
            }
        }
    }
}