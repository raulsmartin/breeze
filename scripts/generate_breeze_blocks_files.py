import os.path

current_directory = os.path.dirname(__file__)

# Block IDs and Types (Change them as desired before running)
blocks = ["acacia_log", "acacia_wood", "stone", "terracotta", "white_concrete"]
types = ["type1", "type2", "type3", "type4", "type5"]


def replace_from_template(template_filename, output_path, block_id, type_id):
    with open(os.path.join(current_directory, 'templates', template_filename), 'r') as file:
        filedata = file.read()

    filedata = filedata.replace('${block_id}', block_id).replace('${type}', type_id)

    with open(output_path, 'w') as file:
        file.write(filedata)


def replace_all_templates(block_id, type_id):
    # Blockstates
    replace_from_template('template_breeze_blockstate.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'assets', 'breeze',
                                       'blockstates', block_id + '_breeze_' + type_id + '.json'), block_id, type_id)

    # Block Models
    replace_from_template('template_' + block_id + '_breeze_block_model.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'assets', 'breeze',
                                       'models', 'block', block_id + '_breeze_' + type_id + '.json'), block_id, type_id)
    replace_from_template('template_' + block_id + '_breeze_block_model.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'assets', 'breeze',
                                       'models', 'block', block_id + '_breeze_' + type_id + '_double.json'),
                          block_id, type_id + '_double')

    # Item Model
    replace_from_template('template_breeze_item_model.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'assets', 'breeze',
                                       'models', 'item', block_id + '_breeze_' + type_id + '.json'), block_id, type_id)

    # Loot Table
    replace_from_template('template_breeze_loot_table.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'data', 'breeze',
                                       'loot_tables', 'blocks', block_id + '_breeze_' + type_id + '.json'), block_id,
                          type_id)

    # Recipe
    replace_from_template('template_breeze_recipe.json',
                          os.path.join(current_directory, '..', 'src', 'main', 'resources', 'data', 'breeze', 'recipes',
                                       block_id + '_breeze_' + type_id + '.json'), block_id, type_id)


for current_block_id in blocks:
    for current_type_id in types:
        replace_all_templates(current_block_id, current_type_id)
