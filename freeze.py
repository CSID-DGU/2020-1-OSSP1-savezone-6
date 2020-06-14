# freeze.py
from tensorflow import keras
import tensorflow as tf

model = keras.models.load_model('/Users/kimwanki/PycharmProjects/untitled3/tflite/tf/model.h5', compile=False)

keras.models.load_model
export_path = '/Users/kimwanki/PycharmProjects/untitled3/tflite/tf/mymodel.pb'
model.save(export_path, save_format="tf")


converter = tf.lite.TFLiteConverter.from_saved_model('/Users/kimwanki/PycharmProjects/untitled3/tflite/tf/mymodel.pb')
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS,
                                       tf.lite.OpsSet.SELECT_TF_OPS]

tflite_model = converter.convert()
open('/Users/kimwanki/PycharmProjects/untitled3/tflite/tf/converted_model.tflite', 'wb').write(tflite_model)
