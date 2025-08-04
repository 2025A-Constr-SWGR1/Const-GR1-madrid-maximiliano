exports.suma = (numeroUno, numeroDos) => {
  const numeroUnoCasteado = Number(numeroUno);
  const numeroDosCasteado = Number(numeroDos);
  
  if (isNaN(numeroUnoCasteado) || isNaN(numeroDosCasteado)) {
    throw new Error('No son números válidos');
  }

  const resultado = numeroUnoCasteado + numeroDosCasteado;
  console.log(`Suma: ${numeroUnoCasteado} + ${numeroDosCasteado} = ${resultado}`);
  return resultado;
}